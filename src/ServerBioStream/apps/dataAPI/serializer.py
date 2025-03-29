from datetime import datetime

from django.db import IntegrityError
from django.utils.timezone import get_current_timezone
from rest_framework import serializers

from .models import Measurement


# Serialize each measurement of the measurements lists 
class GroupSerializer(serializers.Serializer):
    date = serializers.IntegerField()
    type = serializers.CharField()
    value = serializers.FloatField()

# Serialize a data send
class MeasurementsSerializer(serializers.Serializer):
    identifier = serializers.CharField()
    measurements = serializers.ListField(child=GroupSerializer())

    # Create and return a new Measurement instance
    def create(self, validated_data):
        identifier = validated_data.get("identifier", None)
        data = validated_data.get("measurements", None)
        actual_time_zone = get_current_timezone()

        # For each measurement
        for element in data:
            time = datetime.fromtimestamp(
                int(element["date"]) / 1000, tz=actual_time_zone
            )

            model = Measurement(
                experiment=identifier,
                timestamp=time,
                value=element["value"],
                type=element["type"],
            )

            try:
                model.save()
            except IntegrityError as e:
                # Catches integrity exception, which may occur if there is a unique constraint violation (e.g. duplicate unique key).
                print(f"Could not save the model instance. Reason:  {e}")
            except Exception as e:
                # Captures other general exceptions
                print(f"Error when trying to save the model instance. Reason: {e}")
