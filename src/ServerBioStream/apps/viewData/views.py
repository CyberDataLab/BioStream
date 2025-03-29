import json
from django.shortcuts import render
from rest_framework.parsers import JSONParser
from rest_framework.views import APIView
from django.db import IntegrityError
from django.http import JsonResponse
from django.db.models import Min, Max, Count
from venv import logger

from apps.dataAPI.models import Measurement
from django.contrib.auth.decorators import login_required

"""
Method used to get all the experiments for the inital load of view-data.html 
"""
@login_required
def get_data(request):
    experiments = Measurement.objects.values('experiment').annotate(
        start_date=Min('timestamp'),
        end_date=Max('timestamp'),
    )

    for experiment in experiments:
        types = Measurement.objects.filter(experiment=experiment['experiment']).values_list('type', flat=True).distinct()
        sorted_types = sorted(set(types))
        
        # Insert a line break every 5 items
        # Split the list into sublists of 5 elements
        grouped_types = [sorted_types[i:i+5] for i in range(0, len(sorted_types), 5)]
        
        # Join each group of elements with a comma, and between groups add a line break
        experiment['types'] = '\n'.join([', '.join(group) for group in grouped_types])

    return render(request, 'view-data.html', {"measurements": [], "experiments": experiments})

"""
Method used to get all the measurements of an experiment
"""
@login_required
def fetch_measurements(request):
    experiment_ids = request.GET.getlist('experiments[]')

    if experiment_ids:
        measurements = Measurement.objects.filter(experiment__in=experiment_ids)
    else:
        measurements = Measurement.objects.all()

    measurements_data = [
        {
            "experiment": m.experiment,
            "timestamp": m.timestamp.strftime("%d/%m/%Y %H:%M:%S.%f"),
            "type": m.type,
            "value": m.value,
        }
        for m in measurements
    ]

    return JsonResponse({"measurements": measurements_data})

"""
Method used to delete an experiment
"""
@login_required
def delete_experiment(request):

    if request.method == "POST":
        body = json.loads(request.body.decode("utf-8"))
        experimentId = body.get("experimentId", None)

        try:
            experiment = Measurement.objects.filter(experiment=experimentId).delete()
            return JsonResponse(
                {"status": "success", "message": "Study case deleted."}
            )
        except json.JSONDecodeError:
            return JsonResponse(
                {"status": "error", "message": "Invalid JSON format."}
            )
    else:
        return JsonResponse(
            {
                "status": "error",
                "message": "You do not have permissions to delete this case study!",
            }
        )

    return 
