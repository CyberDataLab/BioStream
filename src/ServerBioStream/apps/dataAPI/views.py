import json
from django.shortcuts import render
from rest_framework.parsers import JSONParser
from rest_framework.views import APIView
from django.db import IntegrityError
from django.http import JsonResponse
from django.db.models import Min, Max, Count
from venv import logger

from .models import Measurement
from .serializer import MeasurementsSerializer


# API to receive and store external data
class Measurements_append(APIView):

    def get(self, request):
        return JsonResponse({"detail": "Get not allowed"}, status=405)

    # Only accept post requests
    def post(self, request):
        if request.method == "POST":
            try:
                data = JSONParser().parse(request)
                serializer = MeasurementsSerializer(data=data)
                if serializer.is_valid():
                    serializer.create(serializer.validated_data)
                    return JsonResponse({}, status=201)
                else:
                    return JsonResponse(serializer.errors, status=400)
            except KeyError as e:
                logger.error(f"KeyError: Missing key {e}")
                return JsonResponse({"detail": f"Missing key: {str(e)}"}, status=400)
            except ValueError as e:
                logger.error(f"ValueError: {e}")
                return JsonResponse({"detail": "Invalid value provided"}, status=400)

            except IntegrityError as e:
                logger.error(f"IntegrityError: {e}")
                return JsonResponse({"detail": "Database integrity error"}, status=400)

            except TypeError as e:
                logger.error(f"TypeError: {e}")
                return JsonResponse(
                    {"detail": "Invalid data type provided"}, status=400
                )

            except Exception as e:
                logger.error(f"Invalid JSON, possible connection verification: ")
                return JsonResponse(
                    {"detail": "An unexpected error occurred", "error": str(e)},
                    status=400,
                )
        else:
            return JsonResponse({"detail": "Only post available"}, status=400)
