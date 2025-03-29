from django.shortcuts import render
from apps.authentication.models import UserRoles
from django.contrib.auth import update_session_auth_hash
from django.contrib.auth.decorators import login_required
from django.contrib.auth.hashers import check_password
from django.contrib.auth.models import Group, User
from django.db.models import Min, Max, Count

from django.http import HttpResponseRedirect, JsonResponse

from apps.dataAPI.models import Measurement
import json
from django.views.decorators.csrf import csrf_exempt
from .models import Profile


"""
Method used to get or update the user's profile.
"""
@login_required
def profile(request):
    # Get the user profile
    if request.method == "GET":
        username = request.session.get("user_username", None)
        user = User.objects.get(username=username)

        # Get the user role 
        if user.groups.filter(name=UserRoles.ADMIN).exists():
            role = UserRoles.ADMIN
        else: 
            role = UserRoles.RESEARCHER

        # Get or create profile
        profile, created = Profile.objects.get_or_create(user=user)

        context = {
            "email": user.email,
            "name": user.first_name,
            "surname": user.last_name,
            "username": user.username,
            "avatar": profile.avatar,
            "role": role, 
        }

        return render(request, "profile.html", context)

    # Update the user profile
    elif request.method == "POST":
        username = request.session.get("user_username", None)

        email = request.POST.get("email", None)
        name = request.POST.get("name", None)
        surname = request.POST.get("surname", None)
        oldpassword = request.POST.get("oldp", None)
        newpassword = request.POST.get("newp", None)


        user = User.objects.get(username=username)
        profile, created = Profile.objects.get_or_create(user=user)

        user.first_name = name
        user.last_name = surname

        # Get the user role 
        if user.groups.filter(name=UserRoles.ADMIN).exists():
            role = UserRoles.ADMIN
        else: 
            role = UserRoles.RESEARCHER

        # If there is a new password
        if newpassword:
            if check_password(oldpassword, user.password):
                # Update the new password
                if oldpassword != newpassword:
                    user.set_password(newpassword)
                    update_session_auth_hash(request, user)
                    user.save()
                    profile.save()

                    context = {
                        "email": email,
                        "name": user.first_name,
                        "surname": user.last_name,
                        "username": user.username,
                        "avatar": profile.avatar,
                        "role": role,
                    }
                    return render(request, "profile.html", context)
                
                # Incorrect previous password
                else:
                    context = {
                        "email": email,
                        "name": user.first_name,
                        "surname": user.last_name,
                        "username": user.username,
                        "avatar": profile.avatar,
                        "role": role,
                        "error": "The new password can not be the same as the previous password.",
                    }
                    return render(request, "profile.html", context)
            else:
                context = {
                    "email": email,
                    "name": user.first_name,
                    "surname": user.last_name,
                    "username": user.username,
                    "avatar": profile.avatar,
                    "role": role,
                    "error": "The current password is incorrect.",
                }
                return render(request, "profile.html", context)

        user.save()
        profile.save()

        context = {
            "email": email,
            "name": user.first_name,
            "surname": user.last_name,
            "username": user.username,
            "avatar": profile.avatar,
            "role": role,
        }
        return render(request, "profile.html", context)
    
    else:
        return JsonResponse(
            {"status": "fail", "message": "Invalid request"}, status=400
        )


"""
Ajax method used to update the user avatar
"""
@login_required
def update_avatar(request):
    user = User.objects.get(username=request.user)
    if request.method == "POST":
        avatar = request.FILES.get("avatar")
        if avatar:
            user.profile.avatar = avatar
            user.profile.save()
            user.save()
            return JsonResponse(
                {"status": "success", "avatar_url": user.profile.avatar.url}
            )
        else:
            return JsonResponse(
                {"status": "error", "message": "No se encontró un archivo"}, status=400
            )
    return JsonResponse(
        {"status": "error", "message": "Solicitud inválida"}, status=400
    )

"""
Method used to get, create or update a the user profile
"""
@login_required
def create_user(request):

    # Get a profile
    if request.method == "GET":
        email = request.session.get("user_email")
        return render(request, "new-user.html", {})
    
    # Create or update a profile
    elif request.method == "POST":
        name = request.POST.get("name", None)
        surname = request.POST.get("surname", None)
        password1 = request.POST.get("password1", None)
        password2 = request.POST.get("password2", None)
        email = request.POST.get("email", None)
        username = request.POST.get("username", None)
        role = request.POST.get("role")

        if username and password1:
            
            if User.objects.filter(username=username).exists():
                error_message = "The username chosen is already taken!"
                return render(
                    request,
                    "new-user.html",
                    {"error": error_message},
                )
            if password1 != password2:
                error_message = "The passwords are different!"
                return render(
                    request,
                    "new-user.html",
                    {"error": error_message},
                )
            if role not in UserRoles.get_all_roles():
                error_message = "The role is not among the available roles!"
                return render(
                    request,
                    "new-user.html",
                    {"error": error_message},
                )
            else:
                user = User.objects.create_user(username=username, password=password1)
                user.first_name = name
                user.last_name = surname
                user.email = email
                user.username = username
                group, created = Group.objects.get_or_create(name=role)
                user.groups.add(group)
                user.save()

                profile = Profile(user=user)
                profile.save()

                return HttpResponseRedirect('/users-list')
        else:
            error_message = "A username and password are required!"
            return render(
                request,
                "new-user.html",
                {"error": error_message},
            )

    else:
        return JsonResponse(
            {"status": "fail", "message": "Invalid request"}, status=400
        )
    

"""
Method used to get all users
"""
@login_required
def get_users_list(request):
    if request.method == "GET":
        users = User.objects.all()
        users_with_groups = []

        # Building the user lists
        for user in users:
            group = user.groups.first()
            group_name = group.name if group else "No Group"
            users_with_groups.append({"user": user, "group": group_name})

        email = request.session.get("user_email")
        return render(
            request,
            "users-list.html",
            {"users": users_with_groups, "username": email},
        )
    else:
        return JsonResponse(
            {"status": "fail", "message": "Invalid request"}, status=400
        )

"""
Method used to delete a user.
"""
@login_required
def delete_user(request):

    if request.method == "POST":
        data = json.loads(request.body)
        username = data.get("username", None)
        user = User.objects.get(username=username)

        profile = Profile.objects.filter(user=user).first()
        if profile:
            profile.delete()
        user.delete()

        users = User.objects.all()

        return JsonResponse(
                {"status": "success", "message": "User deleted."}
            )
    
    else:
        return JsonResponse(
            {"status": "fail", "message": "Invalid request"}, status=400
        )

