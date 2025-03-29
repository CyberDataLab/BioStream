from django.urls import path
from django.contrib.auth.views import LogoutView
from apps.authentication.views import CustomLoginView


urlpatterns = [
    path("", CustomLoginView, name="login"),
    path("logout", LogoutView.as_view(next_page="/"), name="logout"),
]