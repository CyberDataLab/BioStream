from django.db import models
from django.contrib.auth.models import Group, User

# Rol managament
class UserRoles:
    ADMIN = "Administrator"
    RESEARCHER = "Researcher"

    CHOICES = [
        (ADMIN, "Administrador"),
        (RESEARCHER, "Researcher"),
    ]

    """Returns a list with the names of all roles"""
    @classmethod
    def get_all_roles(cls):
        return [cls.ADMIN, cls.RESEARCHER]
    
    """Create the groups in the database if they do not exist"""
    @classmethod
    def create_roles(cls, sender, **kwargs):
        for role in cls.get_all_roles():
            Group.objects.get_or_create(name=role)

        # Assign the Administrator group to the superusers
        admin_group, _ = Group.objects.get_or_create(name=cls.ADMIN)
        superusers = User.objects.filter(is_superuser=True)
        for user in superusers:
            user.groups.add(admin_group)
