from django.contrib.auth.models import User

def is_researcher_context(request):
    if not request.user.is_authenticated:
        return {"is_researcher": False}

    is_researcher = request.user.groups.filter(name="researcher").exists()
    return {"is_researcher": is_researcher}