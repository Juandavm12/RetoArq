import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Sistema de ventas para tienda pequeña "Dona Rosa"
 * Todo el sistema esta contenido en una sola clase.
 */
public class TiendaApp {

    // Datos de productos (cada lista guarda un atributo de todos los productos)
    private List<String> productoCodigos = new ArrayList<>();
    private List<String> productoNombres = new ArrayList<>();
    private List<Double> productoPrecios = new ArrayList<>();
    private List<Integer> productoStock = new ArrayList<>();
    private List<String> productoCategorias = new ArrayList<>();

    // Datos de ventas registradas
    private List<String> ventaFechas = new ArrayList<>();
    private List<List<String>> ventaProductos = new ArrayList<>();
    private List<List<Integer>> ventaCantidades = new ArrayList<>();
    private List<List<Double>> ventaPrecios = new ArrayList<>();
    private List<Double> ventaTotales = new ArrayList<>();
    private List<String> ventaMetodosPago = new ArrayList<>();
    private int contadorVentas = 0;

    // Carrito de compras temporal (se limpia en cada venta)
    private List<String> carritoProductos = new ArrayList<>();
    private List<Integer> carritoCantidades = new ArrayList<>();
    private List<Double> carritoPrecios = new ArrayList<>();

    // Datos de la tienda
    private String nombreTienda = "Dona Rosa";
    private String direccionTienda = "Calle Principal #123";
    private String telefonoTienda = "555-1234";
    private double impuesto = 0.19;

    private Scanner scanner = new Scanner(System.in);

    // ==================== MAIN ====================
    public static void main(String[] args) {
        TiendaApp app = new TiendaApp();
        app.cargarProductosIniciales();
        app.ejecutar();
    }

    // ==================== CARGA INICIAL DE DATOS ====================
    public void cargarProductosIniciales() {
        agregarProductoInicial("P001", "Arroz 1kg", 25.50, 50, "Abarrotes");
        agregarProductoInicial("P002", "Frijol 1kg", 32.00, 40, "Abarrotes");
        agregarProductoInicial("P003", "Aceite 1L", 45.90, 30, "Abarrotes");
        agregarProductoInicial("P004", "Leche 1L", 22.00, 60, "Lacteos");
        agregarProductoInicial("P005", "Pan de caja", 38.50, 25, "Panaderia");
        agregarProductoInicial("P006", "Jabon de bano", 18.00, 45, "Higiene");
        agregarProductoInicial("P007", "Refresco 600ml", 15.00, 80, "Bebidas");
        agregarProductoInicial("P008", "Galletas", 12.50, 35, "Snacks");

        System.out.println("=== Productos cargados exitosamente ===\n");
    }

    private void agregarProductoInicial(String codigo, String nombre, double precio, int stock, String categoria) {
        productoCodigos.add(codigo);
        productoNombres.add(nombre);
        productoPrecios.add(precio);
        productoStock.add(stock);
        productoCategorias.add(categoria);
    }

    // ==================== MENU PRINCIPAL ====================
    public void ejecutar() {
        int opcion;
        do {
            System.out.println("========================================");
            System.out.println("   SISTEMA DE VENTAS - " + nombreTienda);
            System.out.println("========================================");
            System.out.println("  1. Realizar venta");
            System.out.println("  2. Ver productos");
            System.out.println("  3. Agregar producto");
            System.out.println("  4. Buscar producto");
            System.out.println("  5. Ver reporte de ventas");
            System.out.println("  0. Salir");
            System.out.println("========================================");
            System.out.print("Seleccione una opcion: ");

            opcion = leerEntero();

            switch (opcion) {
                case 1: realizarVenta(); break;
                case 2: verProductos(); break;
                case 3: agregarProducto(); break;
                case 4: buscarProducto(); break;
                case 5: verReporteVentas(); break;
                case 0: System.out.println("\nGracias por usar el sistema. Hasta luego.\n"); break;
                default: System.out.println("\n[ERROR] Opcion no valida.\n");
            }
        } while (opcion != 0);
    }

    // ==================== REALIZAR VENTA ====================
    public void realizarVenta() {
        System.out.println("\n=== NUEVA VENTA ===");

        // Limpiar carrito
        carritoProductos.clear();
        carritoCantidades.clear();
        carritoPrecios.clear();

        // Agregar productos al carrito
        while (true) {
            System.out.print("Codigo del producto (o 'fin' para terminar): ");
            String codigo = scanner.nextLine().trim();

            if (codigo.equalsIgnoreCase("fin")) {
                break;
            }

            // Buscar el producto por codigo
            int indice = -1;
            for (int i = 0; i < productoCodigos.size(); i++) {
                if (productoCodigos.get(i).equalsIgnoreCase(codigo)) {
                    indice = i;
                    break;
                }
            }

            if (indice == -1) {
                System.out.println("[ERROR] Producto no encontrado.");
                continue;
            }

            // Mostrar info del producto encontrado
            System.out.println("Producto: " + productoNombres.get(indice)
                    + " | Precio: $" + String.format("%.2f", productoPrecios.get(indice))
                    + " | Stock: " + productoStock.get(indice));

            System.out.print("Cantidad: ");
            int cantidad = leerEntero();

            // Validar cantidad
            if (cantidad <= 0) {
                System.out.println("[ERROR] La cantidad debe ser mayor a 0.");
                continue;
            }
            if (cantidad > productoStock.get(indice)) {
                System.out.println("[ERROR] Stock insuficiente. Disponible: " + productoStock.get(indice));
                continue;
            }

            // Agregar al carrito y descontar stock
            carritoProductos.add(productoNombres.get(indice));
            carritoCantidades.add(cantidad);
            carritoPrecios.add(productoPrecios.get(indice));
            productoStock.set(indice, productoStock.get(indice) - cantidad);

            double subtotalItem = cantidad * productoPrecios.get(indice);
            System.out.println("Agregado: " + productoNombres.get(indice)
                    + " x" + cantidad + " = $" + String.format("%.2f", subtotalItem) + "\n");
        }

        // Si el carrito esta vacio, cancelar
        if (carritoProductos.isEmpty()) {
            System.out.println("Venta cancelada - carrito vacio.\n");
            return;
        }

        // Calcular totales
        double subtotal = 0;
        for (int i = 0; i < carritoProductos.size(); i++) {
            subtotal += carritoCantidades.get(i) * carritoPrecios.get(i);
        }
        double montoImpuesto = subtotal * impuesto;
        double total = subtotal + montoImpuesto;

        // Seleccionar metodo de pago
        System.out.println("Metodo de pago: 1) Efectivo  2) Tarjeta");
        System.out.print("Seleccione: ");
        int metodoPago = leerEntero();
        String metodoStr = (metodoPago == 2) ? "Tarjeta" : "Efectivo";

        // Si paga en efectivo, pedir monto
        double cambio = 0;
        if (metodoPago != 2) {
            System.out.print("Monto recibido: $");
            double montoRecibido = leerDecimal();

            if (montoRecibido < total) {
                System.out.println("[ERROR] Monto insuficiente. Se requiere: $" + String.format("%.2f", total));

                // Devolver stock de los productos del carrito
                for (int i = 0; i < carritoProductos.size(); i++) {
                    for (int j = 0; j < productoNombres.size(); j++) {
                        if (productoNombres.get(j).equals(carritoProductos.get(i))) {
                            productoStock.set(j, productoStock.get(j) + carritoCantidades.get(i));
                            break;
                        }
                    }
                }
                System.out.println("Venta cancelada.\n");
                return;
            }
            cambio = montoRecibido - total;
        }

        // Registrar la venta
        contadorVentas++;
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ventaFechas.add(fecha);
        ventaProductos.add(new ArrayList<>(carritoProductos));
        ventaCantidades.add(new ArrayList<>(carritoCantidades));
        ventaPrecios.add(new ArrayList<>(carritoPrecios));
        ventaTotales.add(total);
        ventaMetodosPago.add(metodoStr);

        // Imprimir recibo
        System.out.println("\n========================================");
        System.out.println("          RECIBO DE VENTA");
        System.out.println("  " + nombreTienda);
        System.out.println("  " + direccionTienda);
        System.out.println("  Tel: " + telefonoTienda);
        System.out.println("========================================");
        System.out.println("  Venta #: " + contadorVentas);
        System.out.println("  Fecha: " + fecha);
        System.out.println("  Metodo de pago: " + metodoStr);
        System.out.println("----------------------------------------");
        System.out.printf("  %-18s %5s %10s%n", "Producto", "Cant.", "Subtotal");
        System.out.println("----------------------------------------");

        for (int i = 0; i < carritoProductos.size(); i++) {
            double sub = carritoCantidades.get(i) * carritoPrecios.get(i);
            System.out.printf("  %-18s %5d  $%8.2f%n",
                    carritoProductos.get(i), carritoCantidades.get(i), sub);
        }

        System.out.println("----------------------------------------");
        System.out.printf("  Subtotal:              $%8.2f%n", subtotal);
        System.out.printf("  IVA (%.0f%%):              $%8.2f%n", impuesto * 100, montoImpuesto);
        System.out.printf("  TOTAL:                 $%8.2f%n", total);
        if (metodoPago != 2) {
            System.out.printf("  Cambio:                $%8.2f%n", cambio);
        }
        System.out.println("========================================\n");
    }

    // ==================== VER PRODUCTOS ====================
    public void verProductos() {
        System.out.println("\n=== CATALOGO DE PRODUCTOS ===");
        System.out.printf("%-8s %-18s %-12s %8s %8s%n", "Codigo", "Nombre", "Categoria", "Precio", "Stock");
        System.out.println("---------------------------------------------------------");

        for (int i = 0; i < productoCodigos.size(); i++) {
            System.out.printf("%-8s %-18s %-12s $%7.2f %8d%n",
                    productoCodigos.get(i),
                    productoNombres.get(i),
                    productoCategorias.get(i),
                    productoPrecios.get(i),
                    productoStock.get(i));
        }
        System.out.println();
    }

    // ==================== AGREGAR PRODUCTO ====================
    public void agregarProducto() {
        System.out.println("\n=== AGREGAR NUEVO PRODUCTO ===");

        System.out.print("Codigo: ");
        String codigo = scanner.nextLine().trim();

        // Verificar que no exista
        for (String c : productoCodigos) {
            if (c.equalsIgnoreCase(codigo)) {
                System.out.println("[ERROR] Ya existe un producto con ese codigo.\n");
                return;
            }
        }

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            System.out.println("[ERROR] El nombre no puede estar vacio.\n");
            return;
        }

        System.out.print("Precio: $");
        double precio = leerDecimal();
        if (precio <= 0) {
            System.out.println("[ERROR] El precio debe ser mayor a 0.\n");
            return;
        }

        System.out.print("Stock inicial: ");
        int stock = leerEntero();
        if (stock < 0) {
            System.out.println("[ERROR] El stock no puede ser negativo.\n");
            return;
        }

        System.out.print("Categoria: ");
        String categoria = scanner.nextLine().trim();

        // Guardar en las listas
        productoCodigos.add(codigo);
        productoNombres.add(nombre);
        productoPrecios.add(precio);
        productoStock.add(stock);
        productoCategorias.add(categoria);

        System.out.println("Producto '" + nombre + "' agregado exitosamente.\n");
    }

    // ==================== BUSCAR PRODUCTO ====================
    public void buscarProducto() {
        System.out.println("\n=== BUSCAR PRODUCTO ===");
        System.out.print("Buscar por (1) Codigo  (2) Nombre: ");
        int tipo = leerEntero();

        System.out.print("Termino de busqueda: ");
        String termino = scanner.nextLine().trim().toLowerCase();

        boolean encontrado = false;
        for (int i = 0; i < productoCodigos.size(); i++) {
            boolean coincide;
            if (tipo == 1) {
                coincide = productoCodigos.get(i).toLowerCase().contains(termino);
            } else {
                coincide = productoNombres.get(i).toLowerCase().contains(termino);
            }

            if (coincide) {
                if (!encontrado) {
                    System.out.printf("%-8s %-18s %-12s %8s %8s%n", "Codigo", "Nombre", "Categoria", "Precio", "Stock");
                    System.out.println("---------------------------------------------------------");
                }
                System.out.printf("%-8s %-18s %-12s $%7.2f %8d%n",
                        productoCodigos.get(i),
                        productoNombres.get(i),
                        productoCategorias.get(i),
                        productoPrecios.get(i),
                        productoStock.get(i));
                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("No se encontraron productos.");
        }
        System.out.println();
    }

    // ==================== REPORTE DE VENTAS ====================
    public void verReporteVentas() {
        System.out.println("\n=== REPORTE DE VENTAS ===");

        if (ventaFechas.isEmpty()) {
            System.out.println("No hay ventas registradas.\n");
            return;
        }

        System.out.println("Total de ventas realizadas: " + contadorVentas);
        System.out.println("-------------------------------------------------");

        double totalGeneral = 0;
        Map<String, Integer> productosVendidos = new HashMap<>();
        Map<String, Double> ingresosPorProducto = new HashMap<>();
        Map<String, Integer> ventasPorMetodo = new HashMap<>();

        // Recorrer cada venta
        for (int v = 0; v < ventaFechas.size(); v++) {
            System.out.println("Venta #" + (v + 1)
                    + " | Fecha: " + ventaFechas.get(v)
                    + " | Pago: " + ventaMetodosPago.get(v)
                    + " | Total: $" + String.format("%.2f", ventaTotales.get(v)));

            totalGeneral += ventaTotales.get(v);

            // Contar ventas por metodo de pago
            String metodo = ventaMetodosPago.get(v);
            ventasPorMetodo.put(metodo, ventasPorMetodo.getOrDefault(metodo, 0) + 1);

            // Acumular productos vendidos
            for (int p = 0; p < ventaProductos.get(v).size(); p++) {
                String prod = ventaProductos.get(v).get(p);
                int cant = ventaCantidades.get(v).get(p);
                double precio = ventaPrecios.get(v).get(p);

                productosVendidos.put(prod, productosVendidos.getOrDefault(prod, 0) + cant);
                ingresosPorProducto.put(prod, ingresosPorProducto.getOrDefault(prod, 0.0) + (cant * precio));
            }
        }

        // Resumen general
        System.out.println("-------------------------------------------------");
        System.out.printf("INGRESO TOTAL: $%.2f%n", totalGeneral);

        // Productos mas vendidos
        System.out.println("\n--- Productos mas vendidos ---");
        System.out.printf("%-20s %10s %12s%n", "Producto", "Cantidad", "Ingresos");
        System.out.println("----------------------------------------------");
        for (Map.Entry<String, Integer> entry : productosVendidos.entrySet()) {
            System.out.printf("%-20s %10d  $%10.2f%n",
                    entry.getKey(), entry.getValue(),
                    ingresosPorProducto.get(entry.getKey()));
        }

        // Ventas por metodo de pago
        System.out.println("\n--- Ventas por metodo de pago ---");
        for (Map.Entry<String, Integer> entry : ventasPorMetodo.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ventas");
        }
        System.out.println();
    }

    // ==================== METODOS AUXILIARES ====================
    private int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double leerDecimal() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
