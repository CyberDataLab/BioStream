from django.urls import path

from . import views

urlpatterns = [
    path("profile", views.profile, name="profile"),
    path("update-avatar-ajax/", views.update_avatar, name="update_avatar"),
    path("new-user", views.create_user, name="new_user"),
    path("users-list", views.get_users_list, name="users_list"),
    path("delete-user/", views.delete_user, name="delete_user"),
]