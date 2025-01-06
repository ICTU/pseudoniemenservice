Plus punten:
Java 21  
Heel lief simpel klein applicatie en nog prima in orde te maken omdat het nog niet te laat is!
- weinig complexiteit

-------------------------------
-------------------------------
Beoordeeld op basis van de ISO-25010 maintainability kwaliteiten:
* Modulariteit
* Herbruikbaarheid
* Analyseerbaarheid
* Aanpasbaarheid
* Testbaarheid

Het volgende:

* Niet Modulair:
    - een enkele module

* Moeilijk analyseerbaar
    - teveel verschillende verantwoordelijkheden verzameld in een plek
    - code duplicatie
    - weinig tot geen documentatie (ook m.b.t. OpenAPI contract)
    - geen duidelijke benamingen van classes op basis van verantwoordelijkheid

* Moeilijk herbruikbaar:
    - een enkele module met alle verantwoordelijkheden
    - geen documentatie
    - geen garantie op functionaliteit door ontbreken test coverage

* Moeilijk aanpasbaar:
    - elke aanpassing heeft grote gevolgen door grote stukken code
    - weinig segmentatie

* Moeilijk testbaar:
    - erg weinig segementatie in verantwoordelijkheden
    - teveel op een plek
-------------------------------
-------------------------------

Te verbeteren
README.md mag veel meer informatie bevatten
* wat is het
* waar dient het voor
* hoe werkt het opzetten
* wie is ervoor verantwoordelijk
* etc

Maven project
- versienummers kunnen beter als properties verntraal worden beheerd
- een mutatie test kan iets meer inzicht geven in hoe goed het getest wordt

Documentatie:
* veel complexe cryptography vergt wel het een en ander aan uitleg

Checkstyle & psot-bugs:
- veel warnings die suppressed worden kunnen net zo goed eruit of gefixed worden

Testing
- 3 testen voor een hele applicatie is geen garantie dat de applicatie doet wat het moet doen
- test coverage is erg laag
- MUTATIE coverage is nog lager
- geen duidelijke beschrijving van tests en wat ze moeten doen
- enkel integratie testing en geen unit tests
- geen mocks
- niet makkelijk te testen vanwege
    - onwenselijke instantiaties van on-mockbare objecten
    - onduidelijkheid en verwevenheid in verantwoordelijkheden (teveel in een plek)
- integratie testen betekent simpelweg dat je bij elke aanpassing alle testen moet herzien

Controllers:
- weinig validatie op input!!!
- doen teveel!
- @SneakyThrows probleem
- benaming
- moeilijk testbaar
- moeilijk leesbaar
- niet gedocumenteerd

Services:
- doen teveel! houd het bij controlleren van input
- @SneakyThrows probleem
- benaming
- moeilijk testbaar
- moeilijk leesbaar
- niet gedocumenteerd
- duplicate code

Utils:
- combineren van Byte[] wordt om meerdere plekken gedaan maar niet consistent gebruikmakend van deze util

Configuratie:
- @Component vervangen met @Configuratioe
- zorg dat hier de validatie gedaan wordt i.p.v. constructors van classes die hier gebruik van maken
- test de configuratie

Models:
- te veel suppress warnings
- geen eigen package
- hard coded values
- maak het netter met LOMBOK

OpenAPI:
- documentatie ontbreekt
- maak het netter met LOMBOK

application-properties:
- waarom is PRD logging altijd op DEBUG? 





