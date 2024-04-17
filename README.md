# PRoST

(***P**raktisches **R**echnungssystem für **o**ptimale **S**pesen- und **T**rinkkultur*)

Ein digitales Kaffeekassen-System
der [Fachschaft für Informatik und Mathematik der Universität Passau](https://fsinfo.fim.uni-passau.de/).

## Deploy

Docker-Compose-Environment:

- VITE_API_URL: URL für die Backend-API
- /data : Ordner für Datenbank und Item-Bilder

**Build & Run Compose**:

```bash
docker compose build
```

```bash
docker compose up
```

## Development

**Frontend**:

- ESLint addon

```bash
cd frontend
npm install
npm run dev
```

**Backend**:

- [Google-Java-Codestyle](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
    - Intellij: Settings > Editor > Codestyle > Scheme 
  
Run backend mit LDAP-Container:
```bash
docker build && docker compose up ldap backend
```