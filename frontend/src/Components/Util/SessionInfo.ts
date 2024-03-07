import { login } from "./Queries";

const localCred = localStorage.getItem("cred");
let encodedCredentials = localCred !== null ? localCred : "";

if (encodedCredentials !== "") {
  login(encodedCredentials);
}

export function getEncodedCredentials(): string {
  return encodedCredentials;
}

export function setEncodedCredentials(cred: string) {
  encodedCredentials = cred;
  localStorage.setItem("cred", encodedCredentials);
}

export function resetCredentials() {
  encodedCredentials = "";
  localStorage.setItem("cred", encodedCredentials);
}
