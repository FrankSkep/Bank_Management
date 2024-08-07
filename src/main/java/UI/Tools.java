package UI;

import DAO.ClienteDAO;
import DAO.CuentaBancariaDAO;
import DAO.TransaccionDAO;
import Entidades.CuentaBancaria;
import Entidades.Transaccion;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Tools {

    private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final CuentaBancariaDAO cuentaBancariaDAO = new CuentaBancariaDAO();
    private static final TransaccionDAO transaccionDAO = new TransaccionDAO();

    public static void mouseListenerTable(JTable tabla, JTextField idTF, JTextField nombreTF,
            JTextField correoTF, JTextField telTF) {
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tabla.getSelectedRow();
                if (row != -1) {
                    // Obtener el modelo de la tabla
                    javax.swing.table.TableModel model = tabla.getModel();

                    // Obtener los valores de la fila seleccionada
                    String id = model.getValueAt(row, 0).toString();
                    String nombre = model.getValueAt(row, 1).toString();
                    String correo = model.getValueAt(row, 2).toString();
                    String telefono = model.getValueAt(row, 3).toString();

                    // Mostrar los valores de la fila en el formulario
                    idTF.setText(id);
                    nombreTF.setText(nombre);
                    correoTF.setText(correo);
                    telTF.setText(telefono);
                }
            }
        });
    }

    public static void setImageLabel(JLabel label, String root) {
        label.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    ImageIcon image = new ImageIcon(root);
                    Icon icon = new ImageIcon(image.getImage().getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_DEFAULT));
                    label.setIcon(icon);
                    label.repaint();
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.toString());
                }
            }
        });
    }

    public static void limpiarFormulario(JTextField[] TFs) {
        for (JTextField t : TFs) {
            t.setText("");
        }
    }

    // Llenar tabla de clientes
    public static void entablarClientes(JTable tabla) {
        actualizarTabla(tabla, clienteDAO.obtenerTodosLosClientes(),
                cliente -> new Object[]{cliente.getID(), cliente.getNombre(), cliente.getCorreo(), cliente.getTelefono()},
                "No hay clientes.");
    }

    // Llenar tabla de cuentas bancarias
    public static void entablarCuentas(JTable tabla, int idCliente) {
        List<CuentaBancaria> cuentasList = cuentaBancariaDAO.obtenerCuentasCliente(idCliente);

        actualizarTabla(tabla, cuentasList,
                cuenta -> new Object[]{(Integer) cuenta.getNumeroCuenta(), (Double) cuenta.getSaldo()},
                "Aun no tienes cuentas bancarias.");
    }

    // Llenar tabla de transacciones
    public static void entablarTransacciones(JTable tabla, int idCliente) {

        List<Transaccion> transaccionesList = transaccionDAO.obtenerTransaccionesCliente(idCliente);

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        actualizarTabla(tabla, transaccionesList,
                t -> new Object[]{t.getFecha().format(formato), t.getTipo(), (double) t.getMonto(), t.getDescripcion(), (int) t.getNumCuenta()},
                "Aun no tienes transacciones.");
    }

    private static <T> void actualizarTabla(JTable tabla, List<T> elementos, java.util.function.Function<T, Object[]> mapper, String mensaje) {
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        model.setRowCount(0);

        if (elementos.isEmpty()) {
            JOptionPane.showInternalMessageDialog(null, mensaje);
        } else {
            for (T elemento : elementos) {
                model.addRow(mapper.apply(elemento));
            }
        }
    }

    // Verifica si un correo es valido
    public static boolean validarCorreo(String correo) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern patron = Pattern.compile(emailRegex);
        Matcher matcher = patron.matcher(correo);
        return matcher.matches();
    }

    // Verifica si un telefono es valido
    public static boolean validarTelefono(String telefono) {
        String phoneRegex = "^[0-9]{10}$";
        Pattern patron = Pattern.compile(phoneRegex);
        Matcher matcher = patron.matcher(telefono);
        return matcher.matches();
    }

    // Verifica si una contraseña es valida
    public static boolean esContraseniaValida(String pass) {
        return !(pass.contains(" ") || pass.length() < 6);
    }

    // Recibe un arreglo de strings y verifica si alguno esta vacio
    public static boolean validarCamposVacios(String[] strs) {
        for (String str : strs) {
            if (str.isBlank()) {
                return false;
            }
        }
        return true;
    }

    // Recibe una arreglo de numeros y verifica si alguno no es numerico
    public static boolean sonNumericos(String[] numeros) {
        for (String num : numeros) {
            if (!esNumerico(num)) {
                return false;
            }
        }
        return true;
    }

    // Cambia entre paneles
    public static void showPanel(JPanel p, JPanel mainPanel) {
        p.setSize(1060, 670);
        p.setLocation(0, 0);

        // Remover todos los componentes y sugerir recolección de basura
        mainPanel.removeAll();
        System.gc();

        mainPanel.add(p, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Verifica si un valor es numerico
    public static boolean esNumerico(String str) {
        return str != null && str.matches("\\d*\\.?\\d+");
    }

    // Valida correo y telefono validos
    public static boolean correoTelefonoValidos(String correo, String telefono) {
        if (!Tools.validarCorreo(correo)) {
            JOptionPane.showMessageDialog(null, "Por favor ingresa un correo valido", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!Tools.validarTelefono(telefono)) {
            JOptionPane.showMessageDialog(null, "Por favor ingresa un telefono valido", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
