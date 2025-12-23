# This is an INCORRECT way to store bibliography data
# Bibliography data should be in BibTeX format (.bib file), not a Python file (.py)

bibliography = {
    "albaladejo2024biostream": {
        "title": "BioStream: An open-source solution for biometric data collection through smartwatches",
        "author": "Albaladejo González, Mariano and others",
        "journal": "Journal of Open Source Software",
        "year": "2024",
        "publisher": "The Open Journal",
        "doi": "10.21105/joss.xxxxx"
    },
    "doe2023wearables": {
        "title": "Wearable devices for health monitoring: A comprehensive review",
        "author": "Doe, John and Smith, Jane",
        "journal": "Journal of Medical Technology",
        "volume": "45",
        "number": "3",
        "pages": "123--145",
        "year": "2023",
        "publisher": "Medical Technology Press",
        "doi": "10.1234/jmt.2023.456"
    },
    "smith2023biometric": {
        "title": "Biometric data collection methods in modern research",
        "author": "Smith, Alice and Johnson, Bob",
        "journal": "Research Methods in Biometrics",
        "volume": "12",
        "number": "2",
        "pages": "78--95",
        "year": "2023",
        "publisher": "Biometric Research Society",
        "doi": "10.5678/rmb.2023.789"
    }
}

# PROBLEM: Markdown citation processors (like pandoc-citeproc) cannot read this format
# SOLUTION: Use bibliography.bib with proper BibTeX format instead
