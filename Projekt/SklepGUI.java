package org.example;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SklepGUI extends JFrame {

    // --- ELEMENTY ZAKŁADKI PRODUKTY ---
    private JTable tableProdukty;
    private DefaultTableModel modelProdukty;
    private JTextField txtSzukajProdukt;
    private JTextField txtNazwaProduktu;
    private JTextField txtCenaProduktu;
    private JComboBox<ComboItem> comboKategorie;

    // --- ELEMENTY ZAKŁADKI ZAMÓWIENIA ---
    private JTable tableZamowienia;
    private DefaultTableModel modelZamowienia;
    private JTextField txtSzukajZamowienie;
    private JComboBox<ComboItem> comboKlienci;
    private JComboBox<ComboItem> comboPracownicy;
    private JComboBox<String> comboStatus;

    // --- ELEMENTY ZAKŁADKI RAPORTY ---
    private JTextField rap1DataOd, rap1DataDo;
    private JTextField rap2MinIlosc, rap2MinCena;
    private JTextField rap3DataZatr, rap3Stanowisko;
    private JTextField rap4IdZam, rap4Tytul;

    public SklepGUI() {
        setTitle("System Sklepowy");
        setSize(1100, 750); // Trochę szersze okno
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Dodajemy wszystkie 3 zakładki
        tabbedPane.addTab("1. Produkty", stworzPanelProdukty());
        tabbedPane.addTab("2. Zamówienia", stworzPanelZamowienia());
        tabbedPane.addTab("3. Raporty", stworzPanelRaporty());

        add(tabbedPane);

        // Na start ładujemy dane
        odswiezListyRozwijane();
        odswiezProdukty("");
        odswiezZamowienia("");
    }

    // ==========================================
    // 1. PANEL PRODUKTÓW
    // ==========================================
    private JPanel stworzPanelProdukty() {
        JPanel panel = new JPanel(new BorderLayout());

        // GÓRA: Dodawanie i Szukanie
        JPanel panelTop = new JPanel(new GridLayout(2, 1));

        // Formularz dodawania
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.setBorder(BorderFactory.createTitledBorder("Dodaj Produkt"));

        panelForm.add(new JLabel("Nazwa:"));
        txtNazwaProduktu = new JTextField(12);
        panelForm.add(txtNazwaProduktu);

        panelForm.add(new JLabel("Cena:"));
        txtCenaProduktu = new JTextField(6);
        panelForm.add(txtCenaProduktu);

        panelForm.add(new JLabel("Kategoria:"));
        comboKategorie = new JComboBox<>();
        comboKategorie.setPreferredSize(new Dimension(150, 25));
        panelForm.add(comboKategorie);

        JButton btnDodaj = new JButton("Dodaj");
        btnDodaj.addActionListener(e -> dodajProdukt());
        panelForm.add(btnDodaj);

        panelTop.add(panelForm);

        // Wyszukiwanie i Usuwanie
        JPanel panelSzukaj = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSzukaj.add(new JLabel("Szukaj (nazwa):"));
        txtSzukajProdukt = new JTextField(15);
        JButton btnSzukaj = new JButton("Szukaj");
        btnSzukaj.addActionListener(e -> odswiezProdukty(txtSzukajProdukt.getText()));
        panelSzukaj.add(txtSzukajProdukt);
        panelSzukaj.add(btnSzukaj);

        JButton btnUsun = new JButton("Usuń zaznaczony");
        btnUsun.setBackground(new Color(255, 200, 200));
        btnUsun.addActionListener(e -> usunProdukt());
        panelSzukaj.add(btnUsun);

        panelTop.add(panelSzukaj);
        panel.add(panelTop, BorderLayout.NORTH);

        // Tabela
        modelProdukty = new DefaultTableModel();
        modelProdukty.addColumn("ID");
        modelProdukty.addColumn("Nazwa");
        modelProdukty.addColumn("Cena");
        modelProdukty.addColumn("Kategoria");
        modelProdukty.addColumn("Data Ważności");

        tableProdukty = new JTable(modelProdukty);
        panel.add(new JScrollPane(tableProdukty), BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 2. PANEL ZAMÓWIEŃ
    // ==========================================
    private JPanel stworzPanelZamowienia() {
        JPanel panel = new JPanel(new BorderLayout());

        // GÓRA: Formularz
        JPanel panelTop = new JPanel(new GridLayout(2, 1));

        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nowe Zamówienie"));

        panelForm.add(new JLabel("Klient:"));
        comboKlienci = new JComboBox<>();
        comboKlienci.setPreferredSize(new Dimension(150, 25));
        panelForm.add(comboKlienci);

        panelForm.add(new JLabel("Pracownik:"));
        comboPracownicy = new JComboBox<>();
        comboPracownicy.setPreferredSize(new Dimension(150, 25));
        panelForm.add(comboPracownicy);

        panelForm.add(new JLabel("Status:"));
        String[] statusy = {"W_TRAKCIE", "ZREALIZOWANE", "ANULOWANE"};
        comboStatus = new JComboBox<>(statusy);
        panelForm.add(comboStatus);

        JButton btnDodaj = new JButton("Dodaj Zamówienie");
        btnDodaj.addActionListener(e -> dodajZamowienie());
        panelForm.add(btnDodaj);

        panelTop.add(panelForm);

        // Szukanie
        JPanel panelSzukaj = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSzukaj.add(new JLabel("Filtruj (Status):"));
        txtSzukajZamowienie = new JTextField(10);
        JButton btnSzukaj = new JButton("Szukaj");
        btnSzukaj.addActionListener(e -> odswiezZamowienia(txtSzukajZamowienie.getText()));
        panelSzukaj.add(txtSzukajZamowienie);
        panelSzukaj.add(btnSzukaj);

        JButton btnUsun = new JButton("Usuń zaznaczone");
        btnUsun.setBackground(new Color(255, 200, 200));
        btnUsun.addActionListener(e -> usunZamowienie());
        panelSzukaj.add(btnUsun);

        panelTop.add(panelSzukaj);
        panel.add(panelTop, BorderLayout.NORTH);

        // Tabela
        modelZamowienia = new DefaultTableModel();
        modelZamowienia.addColumn("ID");
        modelZamowienia.addColumn("Data");
        modelZamowienia.addColumn("Klient");
        modelZamowienia.addColumn("Pracownik");
        modelZamowienia.addColumn("Status");

        tableZamowienia = new JTable(modelZamowienia);
        panel.add(new JScrollPane(tableZamowienia), BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 3. PANEL RAPORTÓW (JASPER REPORTS)
    // ==========================================
    private JPanel stworzPanelRaporty() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // RAPORT 1
        JPanel p1 = new JPanel(new GridLayout(4, 1));
        p1.setBorder(BorderFactory.createTitledBorder("1. Sprzedaż (Grupowanie)"));
        p1.add(new JLabel("Data od (YYYY-MM-DD):"));
        rap1DataOd = new JTextField("2025-01-01"); p1.add(rap1DataOd);
        p1.add(new JLabel("Data do (YYYY-MM-DD):"));
        rap1DataDo = new JTextField("2026-12-31"); p1.add(rap1DataDo);
        JButton btnR1 = new JButton("Generuj Raport Sprzedaży");
        btnR1.addActionListener(e -> generujRaportSprzedazy());
        p1.add(btnR1);
        panel.add(p1);

        // RAPORT 2
        JPanel p2 = new JPanel(new GridLayout(4, 1));
        p2.setBorder(BorderFactory.createTitledBorder("2. Wykres Cen (Chart)"));
        p2.add(new JLabel("Ile produktów pokazać:"));
        rap2MinIlosc = new JTextField("5"); p2.add(rap2MinIlosc);
        p2.add(new JLabel("Cena minimalna:"));
        rap2MinCena = new JTextField("10"); p2.add(rap2MinCena);
        JButton btnR2 = new JButton("Generuj Wykres");
        btnR2.addActionListener(e -> generujRaportWykres());
        p2.add(btnR2);
        panel.add(p2);

        // RAPORT 3
        JPanel p3 = new JPanel(new GridLayout(4, 1));
        p3.setBorder(BorderFactory.createTitledBorder("3. Lista Pracowników"));
        p3.add(new JLabel("Zatrudniony po:"));
        rap3DataZatr = new JTextField("2020-01-01"); p3.add(rap3DataZatr);
        p3.add(new JLabel("Stanowisko (np. KASJER%):"));
        rap3Stanowisko = new JTextField("%"); p3.add(rap3Stanowisko);
        JButton btnR3 = new JButton("Generuj Listę");
        btnR3.addActionListener(e -> generujRaportPracownicy());
        p3.add(btnR3);
        panel.add(p3);

        // RAPORT 4
        JPanel p4 = new JPanel(new GridLayout(4, 1));
        p4.setBorder(BorderFactory.createTitledBorder("4. Druk Zamówienia (Formularz)"));
        p4.add(new JLabel("ID Zamówienia:"));
        rap4IdZam = new JTextField("1"); p4.add(rap4IdZam);
        p4.add(new JLabel("Tytuł wydruku:"));
        rap4Tytul = new JTextField("Faktura VAT"); p4.add(rap4Tytul);
        JButton btnR4 = new JButton("Drukuj Zamówienie");
        btnR4.addActionListener(e -> generujRaportFaktura());
        p4.add(btnR4);
        panel.add(p4);

        return panel;
    }

    // ==========================================
    // LOGIKA APLIKACJI (SELECT / INSERT / DELETE)
    // ==========================================

    private void odswiezListyRozwijane() {
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) return;

            comboKategorie.removeAllItems();
            ResultSet rsKat = conn.createStatement().executeQuery("SELECT idKategorii, nazwa FROM Kategoria");
            while (rsKat.next()) { comboKategorie.addItem(new ComboItem(rsKat.getInt(1), rsKat.getString(2))); }

            comboKlienci.removeAllItems();
            ResultSet rsKli = conn.createStatement().executeQuery("SELECT idKlienta, nazwisko || ' ' || imie FROM Klient");
            while (rsKli.next()) { comboKlienci.addItem(new ComboItem(rsKli.getInt(1), rsKli.getString(2))); }

            comboPracownicy.removeAllItems();
            ResultSet rsPra = conn.createStatement().executeQuery("SELECT idPracownika, nazwisko || ' ' || imie FROM Pracownik");
            while (rsPra.next()) { comboPracownicy.addItem(new ComboItem(rsPra.getInt(1), rsPra.getString(2))); }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void odswiezProdukty(String filtr) {
        modelProdukty.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) return;
            String sql = "SELECT p.idProduktu, p.nazwa, p.cena, k.nazwa, p.dataWaznosci " +
                    "FROM Produkt p JOIN Kategoria k ON p.idKategorii = k.idKategorii " +
                    "WHERE p.nazwa LIKE ? ORDER BY p.idProduktu DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + filtr + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelProdukty.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4), rs.getDate(5)});
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage()); }
    }

    private void odswiezZamowienia(String filtr) {
        modelZamowienia.setRowCount(0);
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) return;
            String sql = "SELECT z.idZamowienia, z.dataZamowienia, k.nazwisko, p.nazwisko, z.status " +
                    "FROM Zamowienie z JOIN Klient k ON z.idKlienta = k.idKlienta " +
                    "JOIN Pracownik p ON z.idPracownika = p.idPracownika " +
                    "WHERE z.status LIKE ? ORDER BY z.idZamowienia DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + filtr + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelZamowienia.addRow(new Object[]{rs.getInt(1), rs.getDate(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Błąd: " + e.getMessage()); }
    }

    private void dodajProdukt() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO Produkt (nazwa, cena, idKategorii, dataWaznosci) VALUES (?, ?, ?, CURRENT DATE + 1 YEAR)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, txtNazwaProduktu.getText());
            stmt.setDouble(2, Double.parseDouble(txtCenaProduktu.getText().replace(",", ".")));
            stmt.setInt(3, ((ComboItem) comboKategorie.getSelectedItem()).getId());
            stmt.executeUpdate();
            odswiezProdukty("");
            JOptionPane.showMessageDialog(this, "Dodano produkt!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd dodawania: " + e.getMessage()); }
    }

    private void dodajZamowienie() {
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO Zamowienie (dataZamowienia, status, idKlienta, idPracownika) VALUES (CURRENT DATE, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, (String) comboStatus.getSelectedItem());
            stmt.setInt(2, ((ComboItem) comboKlienci.getSelectedItem()).getId());
            stmt.setInt(3, ((ComboItem) comboPracownicy.getSelectedItem()).getId());
            stmt.executeUpdate();
            odswiezZamowienia("");
            JOptionPane.showMessageDialog(this, "Dodano zamówienie!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd dodawania: " + e.getMessage()); }
    }

    private void usunProdukt() {
        int row = tableProdukty.getSelectedRow();
        if (row == -1) return;
        int id = (int) modelProdukty.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().executeUpdate("DELETE FROM Produkt WHERE idProduktu=" + id);
            odswiezProdukty("");
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Nie można usunąć (może jest w zamówieniu?): " + e.getMessage()); }
    }

    private void usunZamowienie() {
        int row = tableZamowienia.getSelectedRow();
        if (row == -1) return;
        int id = (int) modelZamowienia.getValueAt(row, 0);
        try (Connection conn = DatabaseConnection.connect()) {
            conn.createStatement().executeUpdate("DELETE FROM Platnosc WHERE idZamowienia=" + id);
            conn.createStatement().executeUpdate("DELETE FROM PozycjaZamowienia WHERE idZamowienia=" + id);
            conn.createStatement().executeUpdate("DELETE FROM Zamowienie WHERE idZamowienia=" + id);
            odswiezZamowienia("");
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Błąd usuwania: " + e.getMessage()); }
    }

    // ==========================================
    // LOGIKA JASPER REPORTS
    // ==========================================

    private void generujRaportSprzedazy() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("DataOd", Date.valueOf(rap1DataOd.getText()));
            params.put("DataDo", Date.valueOf(rap1DataDo.getText()));
            uruchomJasper("raport_sprzedaz.jrxml", params);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd danych: " + e.getMessage()); }
    }

    private void generujRaportWykres() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("MinIlosc", Integer.parseInt(rap2MinIlosc.getText()));
            params.put("MinCena", new BigDecimal(rap2MinCena.getText()));
            uruchomJasper("raport_wykres.jrxml", params);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd danych: " + e.getMessage()); }
    }

    private void generujRaportPracownicy() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("MinDataZatrudnienia", Date.valueOf(rap3DataZatr.getText()));
            params.put("SzukaneStanowisko", rap3Stanowisko.getText());
            uruchomJasper("raport_pracownicy.jrxml", params);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd danych: " + e.getMessage()); }
    }

    private void generujRaportFaktura() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("IdZamowienia", Integer.parseInt(rap4IdZam.getText()));
            params.put("Tytul", rap4Tytul.getText());
            uruchomJasper("raport_faktura.jrxml", params);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Błąd danych: " + e.getMessage()); }
    }

    private void uruchomJasper(String plikNazwa, Map<String, Object> parametry) {
        try (Connection conn = DatabaseConnection.connect()) {
            InputStream reportStream = getClass().getResourceAsStream("/" + plikNazwa);
            if (reportStream == null) {
                JOptionPane.showMessageDialog(this, "Nie znaleziono pliku: " + plikNazwa + "\nCzy na pewno jest w folderze 'resources'?");
                return;
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametry, conn);
            JasperViewer.viewReport(jasperPrint, false);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd raportu: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SklepGUI().setVisible(true));
    }
}