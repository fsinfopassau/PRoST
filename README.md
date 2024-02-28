# KdV

(*Kasse des Vertrauens*)

Ein digitales Kaffeekassen-System
der [Fachschaft für Informatik und Mathematik der Universität Passau](https://fsinfo.fim.uni-passau.de/).

## Deployment

Docker-Compose-Environment:

- VITE_API_URL: URL für die Backend-API
- /tmp/kdv : Ordner für Datenbank und Item-Bilder

**Build & Run Compose**:

```bash
docker compose build
```

```bash
docker compose up
```

## Development

**Frontend**:

```bash
cd frontend && npm install && npm run dev
```

**Backend**:

- H2-Datenbank benötigt `/tmp`-Ordner mit Nutzer-Rechten zum lokalen testen!
- Run backend mit Maven...