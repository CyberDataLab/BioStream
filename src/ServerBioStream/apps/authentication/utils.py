from django.contrib.auth.models import Group
from authentication.models import UserRoles

"""Verify if a user belongs to a role"""
def user_has_role(user, role):
    return user.groups.filter(name=role).exists()
