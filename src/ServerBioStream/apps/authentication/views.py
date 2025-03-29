from django.contrib.auth import authenticate, login
from django.shortcuts import render, redirect
from django.contrib import messages

def CustomLoginView(request):
    if request.method == 'POST':
        username = request.POST['username']
        password = request.POST['password']

        user = authenticate(request, username=username, password=password)
        if user is not None:
            login(request, user)  # Start the session

            # Store the user's email in the session
            request.session['user_email'] = user.email
            request.session['user_username'] = username   

            # Redirect to the desired view
            return redirect('get-data')
        else:
            messages.error(request, "Invalid username or password")
    
    return render(request, 'login.html')

