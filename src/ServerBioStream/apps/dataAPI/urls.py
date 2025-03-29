from django.urls import path
from . import views

urlpatterns = [
    path("measurements-api/send/", views.Measurements_append.as_view()),
]