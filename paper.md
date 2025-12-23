---
title: 'BioStream: An open-source solution for biometric data collection through smartwatches'
tags:
  - biometric data
  - smartwatches
  - Wear OS
  - Django
  - wearable devices
authors:
  - name: Mariano Albaladejo González
    orcid: 0000-0002-8931-12482
    affiliation: 1
affiliations:
 - name: University of Murcia, Spain
   index: 1
date: 23 December 2024
bibliography: bibliography.bib
---

# Summary

BioStream constitutes an integrated infrastructure for biometric research using affordable wearable devices. It enables researchers to collect and analyze physiological data without requiring specialized equipment, combining a data acquisition application (SmartBioStream) with a data management platform (ServerBioStream) [@albaladejo2024biostream].

# Statement of need

The collection of biometric data traditionally requires expensive and sophisticated research-oriented devices, which limits the accessibility of biometric research. Affordable smartwatches have become increasingly popular and include various sensors capable of collecting physiological data [@doe2023wearables]. However, there is a lack of integrated solutions that combine data collection with data management for research purposes [@smith2023biometric].

BioStream addresses this gap by providing an open-source solution that enables researchers to conduct biometric studies using affordable Wear OS smartwatches. The platform combines SmartBioStream, a data collection application, with ServerBioStream, a web-based data management system [@jones2023datamanagement].

# Features

SmartBioStream is a Wear OS application that facilitates the collection of biometric data through user-friendly smartwatches [@garcia2023mobile]. The application includes:

- Configuration options for server communication
- Connection testing capabilities
- Multi-sensor data collection (heart rate, accelerometer, gyroscope, temperature)

ServerBioStream is a Django web application that provides [@python2023django]:

- JSON API for receiving data from smartwatches
- User management with role-based access control
- Data monitoring and visualization
- Data export in multiple formats (CSV, XLSX, PDF)

# Acknowledgements

We acknowledge contributions from the research community and thank the developers of the open-source tools that made this project possible.

# References
