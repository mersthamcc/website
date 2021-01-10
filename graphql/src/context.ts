import {PrismaClient} from "@prisma/client";
import {KeycloakContext} from "keycloak-connect-graphql";

export const prisma = new PrismaClient();

export interface Context {
  prisma: PrismaClient,
  kauth: KeycloakContext
}

export function createContext({req}: { req: any }): Context {
  return {
    prisma: prisma,
    kauth: new KeycloakContext({req}),
  };
}
