import os

from django.db import models
from django.contrib.auth.models import User
from django.db import models
from django.utils.translation import gettext_lazy as _


def user_avatar_upload_path(instance, filename):
    # Extract file extension
    ext = filename.split(".")[-1]

    # Build the new filename (avatar.ext)
    new_filename = f"avatar.{ext}"

    # Return the custom path
    return os.path.join(f"images/avatars/{instance.user.username}", new_filename)


class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    avatar = models.ImageField(
        verbose_name=_("Avatar title"),
        upload_to=user_avatar_upload_path,
        default="images/profile.png",
    )

    def __str__(self):
        return self.user.username
