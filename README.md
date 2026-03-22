# System Zarządzania Sklepem - Projekt Bazy Danych

## Opis projektu
Aplikacja desktopowa połączona z relacyjną bazą danych, symulująca działanie sklepu stacjonarnego/internetowego. System umożliwia kompleksowe zarządzanie asortymentem, obsługę zamówień klientów, logistykę magazynową oraz ewidencję pracowników z podziałem na role (Kasjer, Magazynier). 

Projekt został zrealizowany w ramach zaliczenia przedmiotu Bazy Danych. Składa się z części bazodanowej (skrypty SQL) oraz aplikacji klienckiej (GUI w Javie).

## Technologie
* **Baza danych:** IBM Db2 (uruchamiana w środowisku Docker)
* **Język aplikacji:** Java 21
* **Interfejs graficzny:** Java Swing
* **Sterownik bazy:** IBM Data Server Driver for JDBC and SQLJ
* **Zarządzanie zależnościami:** Maven
* **Raportowanie:** JasperReports

## Struktura Bazy Danych (Logika Biznesowa)
System opiera się na znormalizowanym modelu relacyjnym, w skład którego wchodzą następujące moduły:
* **Sprzedaż i Klienci:** Tabele `Klient`, `Zamowienie`, `PozycjaZamowienia`, `Platnosc`.
* **Katalog Produktów:** Tabele `Produkt`, `Kategoria`. Zaimplementowano tu m.in. związek unarny pozwalający na definiowanie zamienników dla produktów.
* **Zarządzanie Personelem:** Implementacja hierarchii (nadtyp/podtyp) – tabela `Pracownik` z wyszczególnieniem na specyficzne role: `Kasjer` oraz `Magazynier`.
* **Logistyka i Magazyn:** Tabele `Dostawca`, `Dostawa`, `Magazyn`.

W bazie wykorzystano zaawansowane obiekty programistyczne, takie jak **Procedury składowane** (np. do automatycznej przeceny kategorii) oraz **Wyzwalacze (Triggery)** kontrolujące spójność biznesową.


## Instrukcja uruchomienia (Dla Prowadzącego)

Aby poprawnie uruchomić projekt i przetestować jego działanie, postępuj zgodnie z poniższymi krokami.

### Krok 1: Konfiguracja Bazy Danych (IBM Db2)
Aplikacja wymaga działającego serwera IBM Db2 nasłuchującego na porcie `50000`.

1. Upewnij się, że kontener z bazą Db2 jest uruchomiony.
2. Połącz się z bazą w swoim kliencie SQL (np. DataGrip / DBeaver) używając konta administratora (domyślnie `db2inst1`).
3. Otwórz dołączony do projektu plik **`skrypt_bazy.sql`**.
4. Uruchom skrypt w całości. Skrypt ten automatycznie:
   * Wyczyści stare tabele (jeśli istnieją).
   * Utworzy nową strukturę tabel (DDL).
   * Załaduje paczkę danych testowych (DML).
   * Utworzy wymagane procedury i triggery.

### Krok 2: Konfiguracja i uruchomienie Aplikacji Java
1. Otwórz folder z projektem w środowisku IDE (np. IntelliJ IDEA).
2. Pobierz zależności Maven (plik `pom.xml`).
3. Przejdź do pliku `src/main/java/org/example/DatabaseConnection.java`.
4. Sprawdź, czy stała `PASSWORD` odpowiada hasłu skonfigurowanemu w Twoim środowisku Docker (zabezpieczenie połączenia bazuje na `authentication=SERVER`).
5. Uruchom klasę główną: **`SklepGUI.java`**.

## Zawartość paczki zaliczeniowej
* `/src` - kod źródłowy aplikacji klienckiej (Java).
* `skrypt_bazy.sql` - kompletny zrzut struktury i danych bazy.
* `Dokumentacja_Projektu.pdf` - szczegółowy opis schematów E-R, relacji, diagramów UML oraz zestawienie zapytań SQL.
