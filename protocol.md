# Media Rating Platform
**David Nawloka if24b015**

Git: https://github.com/DavidNawloka/Media-Rating-Platform

---

## Architektur

Das Projekt folgt einer 3-Layered Architecture. Controller-Klassen repräsentieren die Presentation-Layer. Sie nehmen Requests entgegen und beantworten diese mit Responses. Um Requests zu bearbeiten, verwenden sie sogenannte Service-Klassen. Diese bilden die Business-Layer. In ihnen befindet sich die konkrete Logik des Programms sowie alle Regeln. Zu guter Letzt verwenden diese wiederum Repository-Klassen. Diese bilden die Data-Layer und kommunizieren mit der Datenbank, um Daten persistent zu speichern oder diese abzurufen. Pro Datenbank-Modell gibt es ein Repository, das sich um dieses kümmert.

Das Projekt kann mit folgendem Command gestartet werden:
```
sudo docker compose up
```

### Typischer Request-Flow

```
Client → Controller → Service → Repository → Datenbank
                                    ↓
Client ← Controller ← Service ← Repository ← Datenbank
```

---

## Datenbank Schema

Die Datenbank besteht aus 7 Tabellen:

| Tabelle | Beschreibung |
|---------|-------------|
| **users** | Benutzerkonten mit Username, Passwort (gehasht), Email und optionalem Lieblingsgenre |
| **sessions** | Authentifizierungs-Tokens mit Ablaufdatum |
| **genres** | 12 vordefinierte Genres (Action, Comedy, Drama, etc.) |
| **media** | Filme, Serien und Spiele mit Metadaten |
| **media_genres** | Verknüpfungstabelle für Media-Genre Beziehung (n:m) |
| **ratings** | Bewertungen (1-5 Sterne) mit optionalem Kommentar |
| **rating_likes** | Likes auf Bewertungen anderer Nutzer |
| **favorites** | Favorisierte Medien pro Nutzer |


---

## API Endpoints

### Auth (`/api/users/`)
| Methode | Endpoint | Beschreibung |
|---------|----------|-------------|
| POST | `/register` | Neuen Benutzer registrieren |
| POST | `/login` | Einloggen und Token erhalten |

### Media (`/api/media/`)
| Methode | Endpoint | Beschreibung |
|---------|----------|-------------|
| GET | `/` | Alle Medien auflisten (mit Filteroptionen) |
| POST | `/` | Neues Medium erstellen |
| GET | `/{id}` | Medium mit Bewertungen abrufen |
| PUT | `/{id}` | Medium bearbeiten (nur Ersteller) |
| DELETE | `/{id}` | Medium löschen (nur Ersteller) |
| POST | `/{id}/rate` | Medium bewerten |
| POST | `/{id}/favorite` | Zu Favoriten hinzufügen |
| DELETE | `/{id}/favorite` | Aus Favoriten entfernen |

### Ratings (`/api/ratings/`)
| Methode | Endpoint | Beschreibung |
|---------|----------|-------------|
| PUT | `/{id}` | Bewertung bearbeiten (nur Ersteller) |
| DELETE | `/{id}` | Bewertung löschen (nur Ersteller) |
| POST | `/{id}/like` | Bewertung liken |
| POST | `/{id}/confirm` | Kommentar bestätigen/sichtbar machen |

### User (`/api/users/`)
| Methode | Endpoint | Beschreibung |
|---------|----------|-------------|
| GET | `/{id}/profile` | Eigenes Profil abrufen |
| PUT | `/{id}/profile` | Profil bearbeiten |
| GET | `/{id}/ratings` | Eigene Bewertungen abrufen |
| GET | `/{id}/favorites` | Eigene Favoriten abrufen |
| GET | `/{id}/recommendations` | Empfehlungen erhalten |
| GET | `/leaderboard` | Top-Nutzer nach Aktivität |

---

## Unit Test Coverage

### Warum diese Tests?

Die Tests konzentrieren sich auf die Service-Layer, da dort die gesamte Business-Logik liegt. Controller und Repositories werden durch Integration Tests abgedeckt.

### AuthServiceTest (8 Tests)
- **Registrierung**: Testet erfolgreiche Registrierung sowie Validierungsfehler (leerer Username, zu kurzes Passwort, doppelter Username/Email)
- **Login**: Testet erfolgreichen Login sowie Fehlerfälle (falsches Passwort, unbekannter User)
- **Warum**: Authentifizierung ist sicherheitskritisch. Fehler hier können zu unbefugtem Zugriff führen.

### MediaServiceTest (6 Tests)
- **Erstellen**: Testet erfolgreiche Erstellung und Validierung (leerer Titel, ungültiges Genre)
- **Abrufen**: Testet erfolgreichen Abruf und Handling von nicht existierenden Medien
- **Löschen**: Testet Owner-Check (nur Ersteller darf löschen)
- **Warum**: Media ist die zentrale Entität. Ownership-Checks verhindern unbefugte Änderungen.

### RatingServiceTest (13 Tests)
- **Erstellen**: Testet Validierung der Sterne (1-5), doppelte Bewertungen verhindern
- **Bearbeiten/Löschen**: Testet Owner-Checks
- **Likes**: Testet Like-Funktionalität und Duplikat-Verhinderung
- **Kommentar-Bestätigung**: Testet Sichtbarkeitslogik
- **Warum**: Ratings haben komplexe Regeln (Sterne-Range, Ownership, Likes, Confirmation). Hier passieren die meisten Edge Cases.

### UserServiceTest (5 Tests)
- **Profil**: Testet Abruf mit Statistiken und Fehlerbehandlung
- **Leaderboard**: Testet korrekte Sortierung
- **Update**: Testet Validierung und Duplikat-Checks
- **Warum**: Profil-Statistiken müssen korrekt berechnet werden.

### FavoriteServiceTest (4 Tests)
- **Hinzufügen/Entfernen**: Testet CRUD und Duplikat-Verhinderung
- **Warum**: Einfache Logik, aber Duplikate müssen verhindert werden.

### Integration Tests (2 Tests)
- **AuthIntegrationTest**: Kompletter Register-Login-Flow mit echter Datenbank
- **MediaRatingIntegrationTest**: End-to-End Media-Erstellung mit Authentifizierung
- **Warum**: Stellt sicher, dass alle Layer zusammenarbeiten. Verwendet TestContainers für echte PostgreSQL-Instanz.

---

## SOLID Prinzipien

### Single Responsibility Principle 

Jede Klasse im Projekt hat genau eine Verantwortung und somit nur einen Grund, sich zu ändern. Der AuthController kümmert sich ausschließlich um HTTP-Requests und Responses für Authentifizierung. Der AuthService enthält nur die Business-Logik für Registrierung, Login und Session-Verwaltung. Das UserRepository ist einzig für Datenbankoperationen bezüglich User zuständig. Der AuthValidationService validiert nur Eingabedaten. Wenn sich Validierungsregeln ändern, muss nur der AuthValidationService angepasst werden. Wenn sich das Datenbankschema ändert, muss nur das entsprechende Repository geändert werden. Diese klare Trennung macht den Code wartbar und testbar.

### Dependency Inversion Principle 

Der AuthService erstellt seine Abhängigkeiten nicht selbst, sondern bekommt UserRepository und SessionRepository durch den Konstruktor injiziert. Dadurch ist der AuthService nicht an konkrete Implementierungen gebunden. Der größte Vorteil zeigt sich beim Testen: In Unit Tests können Mock-Objekte injiziert werden, um die Business-Logik isoliert zu testen. In Integration Tests werden die echten Repository-Implementierungen verwendet. Diese lose Kopplung macht den Code flexibel und austauschbar.

---

## Aufgetretene Probleme und Lösungen

### Problem 1: Transaction Management
**Problem**: Mehrere zusammengehörige Datenbankoperationen (z.B. Media erstellen + Genres verknüpfen) konnten bei Teilfehlern zu inkonsistenten Daten führen. 
**Lösung**: Refactor des ganzen Codes und Implementation des Unit of Work Patterns. Alle Operationen werden in einer Transaktion gebündelt. Bei Fehlern wird automatisch rollback ausgeführt.

### Problem 2: Java Versionen
**Problem**: Das Testen des Projekts funktionierte mit Java 21 jedoch nicht mit Java 25.
**Lösung**: Ich musste die neuesten Versionen sämtlicher Dependencies verwenden, um dieses Problem zu vermeiden.

---

## Zeitaufwand

| Bereich | Geschätzter Aufwand |
|---------|---------------------|
| Projektsetup & Architektur | 3 Stunden           |
| Datenbank Schema Design | 2 Stunden           |
| Authentication (Register/Login) | 4 Stunden           |
| Media CRUD | 10 Stunden          |
| Rating System | 11 Stunden          |
| User Profile & Favorites | 8 Stunden           |
| Recommendations & Leaderboard | 7 Stunden           |
| Unit Tests | 11 Stunden          |
| Integration Tests | 3 Stunden           |
| Bug Fixes & Refactoring | 4 Stunden           |
| Dokumentation | 2 Stunden           |
| **Gesamt** | **~65 Stunden**     |

---

## Zusätzliche Hinweise

- Die Git-History dokumentiert den Entwicklungsverlauf im Detail
- Alle Endpoints sind in der Postman Collection dokumentiert
- SQL Injection wird durch PreparedStatements verhindert
- Passwörter werden niemals im Klartext gespeichert oder zurückgegeben
