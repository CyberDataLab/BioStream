from django.urls import path
from . import views

urlpatterns = [
    path('get-data', views.get_data, name='get-data'),
    path('fetch-measurements/', views.fetch_measurements, name='fetch_measurements'),
    path('delete-experiment/', views.delete_experiment, name='delete_experiment'),
]