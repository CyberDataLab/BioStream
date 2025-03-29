from django.core.management.base import BaseCommand
from django.contrib.auth.models import User, Group
from django.core.exceptions import ValidationError
from apps.users.models import Profile

class Command(BaseCommand):
    help = 'Create an administrator user'

    def add_arguments(self, parser):
        """
        Add required arguments for user creation.
        """
        parser.add_argument('name', type=str, help='User first name')
        parser.add_argument('surname', type=str, help='User last name')
        parser.add_argument('username', type=str, help='Username')
        parser.add_argument('email', type=str, help='User email')
        parser.add_argument('password1', type=str, help='User password')
        parser.add_argument('password2', type=str, help='Confirm user password')

    def handle(self, *args, **kwargs):
        """
        Handle the user creation logic with error checking.
        """
        name = kwargs['name']
        surname = kwargs['surname']
        username = kwargs['username']
        email = kwargs['email']
        password1 = kwargs['password1']
        password2 = kwargs['password2']
        role = "Administrator"  # Fixed role assignment

        # Check if the username already exists
        if User.objects.filter(username=username).exists():
            self.stderr.write(self.style.ERROR("The username chosen is already taken!"))
            return

        # Check if passwords match
        if password1 != password2:
            self.stderr.write(self.style.ERROR("The passwords are different!"))
            return

        # Create user and assign role
        user = User.objects.create_user(username=username, password=password1)
        user.first_name = name
        user.last_name = surname
        user.email = email
        user.save()

        # Assign the user to the Administrator group (create it if it doesn't exist)
        group, _ = Group.objects.get_or_create(name=role)
        user.groups.add(group)
        user.save()

        # Ensure Profile model exists before using it
        try:
            profile = Profile(user=user)
            profile.save()
        except ImportError:
            self.stderr.write(self.style.ERROR("Profile model is not found. Make sure it's correctly imported."))

        self.stdout.write(self.style.SUCCESS(f'User {username} created successfully with role {role}'))

