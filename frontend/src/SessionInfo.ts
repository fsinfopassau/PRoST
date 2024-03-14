import { login } from "./Queries";

const firstCred = localStorage.getItem("cred");
let encodedCredentials = firstCred !== null ? firstCred : "";

if (encodedCredentials !== "") {
  login(encodedCredentials);
}

export function getEncodedCredentials(): string {
  return encodedCredentials;
}

export function setEncodedCredentials(cred: string) {
  encodedCredentials = cred !== null ? cred : "";
  localStorage.setItem("cred", encodedCredentials);
}

export function getSessionUserName(): string {
  return window.atob(encodedCredentials).split(":")[0];
}

export function resetCredentials() {
  encodedCredentials = "";
  localStorage.setItem("cred", encodedCredentials);
}

export function validCredentials(): boolean {
  // Platzhalter für Validierung (Sowie der Rest von Basic-Auth🙃..)
  return encodedCredentials !== "";
}
