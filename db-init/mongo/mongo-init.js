// Ce script sera exécuté au premier démarrage du conteneur MongoDB.
// Sélectionne la base de données 'diabete_notes_db'. Si elle n'existe pas, elle sera créée.
db = db.getSiblingDB('diabete_notes_db');

// Crée la collection 'notes' explicitement (bonne pratique).
db.createCollection('notes');

// Insère les documents de test dans la collection 'notes'.
// On utilise ISODate() pour s'assurer que les chaînes de date sont bien stockées comme des types Date BSON.
db.notes.insertMany([
 {
   "patientId": 1,
   "date": ISODate("2024-12-01T10:00:00.000Z"),
   "note": "Le patient déclare qu'il se sent très bien. Poids égal ou inférieur au poids recommandé"
 },
 {
   "patientId": 2,
   "date": ISODate("2024-12-02T11:00:00.000Z"),
   "note": "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint également que son audition est anormale dernièrement"
 },
 {
    "patientId": 2,
    "date": ISODate("2024-12-02T11:00:00.000Z"),
    "note": "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois Il remarque également que son audition continue d'être anormale"
  },
 {
   "patientId": 3,
   "date": ISODate("2024-12-03T14:30:00.000Z"),
   "note": "Le patient déclare qu'il fume depuis peu"
 },
 {
    "patientId": 3,
    "date": ISODate("2024-12-03T14:30:00.000Z"),
    "note": "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière Il se plaint également de crises d’apnée respiratoire anormales Tests de laboratoire indiquant un taux de cholestérol LDL élevé"
  },
 {
   "patientId": 4,
   "date": ISODate("2024-12-04T16:00:00.000Z"),
   "note": "Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il se plaint également d’être essoufflé Tests de laboratoire indiquant que les anticorps sont élevés Réaction aux médicaments"
 },
 {
    "patientId": 4,
    "date": ISODate("2024-12-04T16:00:00.000Z"),
    "note": "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps"
  },
   {
        "patientId": 4,
        "date": ISODate("2024-12-04T16:00:00.000Z"),
        "note": "Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C supérieure au niveau recommandé"
    },
    {
        "patientId": 4,
        "date": ISODate("2024-12-04T16:00:00.000Z"),
        "note": "Taille, Poids, Cholestérol, Vertige et Réaction"
    }
]);