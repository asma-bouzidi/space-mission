# UML Class Diagram (Initial Draft)

## Planned high-level structure

- Spacecraft (abstract)
  - CommunicationSatellite
  - ObservationSatellite
  - NavigationSatellite
  - ScientificSatellite
  - ExplorationSpacecraft

- Mission
- MissionState
- User
- Role
- Telemetry
- Anomaly
- Resource
- CommunicationLog
- Report

## Relationships
- A mission can contain multiple spacecraft
- A user can have multiple roles
- A spacecraft emits telemetry
- A mission may trigger anomalies
- A resource can be allocated to missions
- Communication logs track commands and outcomes

This document will be expanded into a formal UML diagram during Phase 2 and Phase 5.
