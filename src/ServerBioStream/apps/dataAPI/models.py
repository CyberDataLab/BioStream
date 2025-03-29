from django.db import models
from django.utils.timezone import get_current_timezone, now


class Measurement(models.Model):
    experiment = models.CharField(max_length=60, default="")
    timestamp = models.DateTimeField(default=now)
    type = models.CharField(max_length=60, default="")
    value = models.FloatField(default=0.0)

    def __str__(self):
        return (
            "|timestamp: "
            + str(self.timestamp.astimezone(get_current_timezone()))
            + ", type: "
            + str(self.type)
            + ", experiment: "
            + str(self.experiment)
            + ", value: "
            + str(self.value)
        )
