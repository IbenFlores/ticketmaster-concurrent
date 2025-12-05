package com.basededatosii.ticketmaster_concurrent.cli;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.Carrito;
import com.basededatosii.ticketmaster_concurrent.model.Compra;
import com.basededatosii.ticketmaster_concurrent.model.Entrada;
import com.basededatosii.ticketmaster_concurrent.model.Evento;
import com.basededatosii.ticketmaster_concurrent.model.Usuario;
import com.basededatosii.ticketmaster_concurrent.model.Zona;
import com.basededatosii.ticketmaster_concurrent.service.AsientoService;
import com.basededatosii.ticketmaster_concurrent.service.CarritoService;
import com.basededatosii.ticketmaster_concurrent.service.CompraService;
import com.basededatosii.ticketmaster_concurrent.service.EntradaService;
import com.basededatosii.ticketmaster_concurrent.service.EventoService;
import com.basededatosii.ticketmaster_concurrent.service.TransaccionService;
import com.basededatosii.ticketmaster_concurrent.service.UsuarioService;
import com.basededatosii.ticketmaster_concurrent.service.ZonaService;

@Component
public class TicketmasterCLI implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    private final ZonaService zonaService;
    private final AsientoService asientoService;
    private final TransaccionService transaccionService;
    private final CarritoService carritoService;
    private final CompraService compraService;
    private final EntradaService entradaService;

    // Colores
    private static final String RESET = "\033[0m";
    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String BLUE = "\033[0;34m";
    private static final String CYAN = "\033[0;36m";

    public TicketmasterCLI(UsuarioService usuarioService, EventoService eventoService, ZonaService zonaService,
                           AsientoService asientoService, TransaccionService transaccionService, 
                           CarritoService carritoService, CompraService compraService, EntradaService entradaService) {
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
        this.zonaService = zonaService;
        this.asientoService = asientoService;
        this.transaccionService = transaccionService;
        this.carritoService = carritoService;
        this.compraService = compraService;
        this.entradaService = entradaService;
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(this::iniciarTerminal).start();
    }

    private void iniciarTerminal() {
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        Scanner scanner = new Scanner(System.in);
        System.out.println(CYAN + "\n=============================================");
        System.out.println("   BIENVENIDO A TICKETMASTER CONCURRENT CLI");
        System.out.println("=============================================" + RESET);

        while (true) {
            System.out.println("\n1. Iniciar Sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1": login(scanner); break;
                case "2": registrarse(scanner); break;
                case "3":
                    System.out.println(YELLOW + "Apagando el sistema... ¡Hasta luego!" + RESET);
                    System.exit(0);
                    return;
                default: System.out.println(RED + "Opción inválida." + RESET);
            }
        }
    }

    private void login(Scanner scanner) {
        System.out.print("\nCorreo: ");
        String correo = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        
        try {
             Usuario usuario = ((com.basededatosii.ticketmaster_concurrent.service.impl.UsuarioServiceImpl) usuarioService).obtenerUsuarioPorCorreo(correo);

            if (!usuario.getContrasenaHash().equals(password)) {
                System.out.println(RED + "Contraseña incorrecta." + RESET);
                return;
            }

            System.out.println(GREEN + "Bienvenido, " + usuario.getNombreCompleto() + "!" + RESET);
            
            String rol = usuario.getRol();
            if ("ADMIN".equalsIgnoreCase(rol)) {
                menuAdmin(scanner, usuario);
            } else {
                menuCliente(scanner, usuario);
            }

        } catch (Exception e) {
            System.out.println(RED + "Usuario no encontrado." + RESET);
        }
    }

    private void registrarse(Scanner scanner) {
        System.out.println(YELLOW + "\n--- REGISTRO DE USUARIO ---" + RESET);
        System.out.print("Nombre Completo: ");
        String nombre = scanner.nextLine();
        System.out.print("Correo: ");
        String correo = scanner.nextLine();
        System.out.print("Contraseña: ");
        String pass = scanner.nextLine();
        
        System.out.println("Tipo de cuenta: 1. Cliente | 2. Administrador");
        System.out.print("Seleccione: ");
        String tipo = scanner.nextLine();
        String rol = "2".equals(tipo) ? "ADMIN" : "CLIENTE";

        Usuario u = new Usuario();
        u.setNombreCompleto(nombre);
        u.setCorreo(correo);
        u.setContrasenaHash(pass);
         u.setRol(rol); 

        try {
            usuarioService.crearUsuario(u);
            System.out.println(GREEN + "¡Usuario registrado como " + rol + "!" + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void menuAdmin(Scanner scanner, Usuario admin) {
        System.out.println(BLUE + "Modo Administrador activado." + RESET);
        while (true) {
            System.out.println(BLUE + "\n--- PANEL DE ADMINISTRADOR ---" + RESET);
            System.out.println("1. Crear Evento");
            System.out.println("2. Agregar Zona a Evento");
            System.out.println("3. Agregar Asientos a Zona (Lote)");
            System.out.println("4. Liberar Asientos Expirados");
            System.out.println("5. Cerrar Sesión");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1": crearEvento(scanner); break;
                case "2": crearZona(scanner); break;
                case "3": crearAsientosLote(scanner); break;
                case "4": liberarExpirados(); break;
                case "5": return;
                default: System.out.println(RED + "Opción inválida." + RESET);
            }
        }
    }

    private void crearEvento(Scanner scanner) {
        try {
            System.out.print("Nombre del Evento: ");
            String nombre = scanner.nextLine();
            System.out.print("Lugar: ");
            String lugar = scanner.nextLine();
            
            Evento e = new Evento();
            e.setNombre(nombre);
            e.setLugar(lugar);
            e.setFechaEvento(java.time.LocalDateTime.now().plusMonths(1));
            e.setEstado("ACTIVO");
            e.setDescripcion("Evento creado vía CLI");
            
            eventoService.crearEvento(e);
            System.out.println(GREEN + "Evento creado con ID: " + e.getEventoId() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void crearZona(Scanner scanner) {
        try {
            listarEventos();
            System.out.print("ID del Evento: ");
            Long eventoId = Long.parseLong(scanner.nextLine());
            System.out.print("Nombre de la Zona: ");
            String nombre = scanner.nextLine();
            System.out.print("Capacidad Total: ");
            int cap = Integer.parseInt(scanner.nextLine());

            Evento evento = eventoService.obtenerEventoPorId(eventoId);
            Zona z = new Zona();
            z.setNombre(nombre);
            z.setCapacidad(cap);
            z.setEvento(evento);
            
            zonaService.crearZona(z);
            System.out.println(GREEN + "Zona creada con ID: " + z.getZonaId() + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void crearAsientosLote(Scanner scanner) {
        try {
            listarEventos();
            System.out.print("ID del Evento: ");
            Long eventoId = Long.parseLong(scanner.nextLine());
            
            List<Zona> zonas = zonaService.obtenerZonasPorEvento(eventoId);
            System.out.println("\n--- ZONAS ---");
            for (Zona z : zonas) {
                int ocupados = asientoService.obtenerAsientosPorZona(z.getZonaId()).size();
                System.out.printf("ID: %d | %s | Capacidad: %d | Creados: %d\n", z.getZonaId(), z.getNombre(), z.getCapacidad(), ocupados);
            }

            System.out.print("\nID de la Zona: ");
            Long zonaId = Long.parseLong(scanner.nextLine());
            
            Zona zona = zonaService.obtenerZonaPorId(zonaId);
            int asientosActuales = asientoService.obtenerAsientosPorZona(zonaId).size();

            System.out.print("Fila: ");
            String fila = scanner.nextLine();
            System.out.print("Cantidad: ");
            int cant = Integer.parseInt(scanner.nextLine());

            if (asientosActuales + cant > zona.getCapacidad()) {
                System.out.println(RED + "Error: Excede la capacidad máxima de la zona (" + zona.getCapacidad() + ")." + RESET);
                return;
            }

            System.out.print("Precio: ");
            BigDecimal precio = new BigDecimal(scanner.nextLine());

            System.out.println("Generando asientos...");
            for (int i = 1; i <= cant; i++) {
                Asiento a = new Asiento();
                a.setZona(zona);
                a.setFila(fila);
                a.setNumeroAsiento(String.valueOf(i));
                a.setEstado("DISPONIBLE");
                a.setPrecio(precio);
                asientoService.crearAsiento(a);
            }
            System.out.println(GREEN + "¡Éxito! Asientos creados." + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }
    
    private void liberarExpirados() {
        if(transaccionService.liberarAsientosExpirados()) {
            System.out.println(GREEN + "Asientos expirados liberados." + RESET);
        } else {
            System.out.println(YELLOW + "Nada para liberar." + RESET);
        }
    }

    private void menuCliente(Scanner scanner, Usuario cliente) {
        System.out.println(CYAN + "Modo Cliente activado." + RESET);
        while (true) {
            System.out.println(CYAN + "\n--- MENÚ CLIENTE (" + cliente.getNombreCompleto() + ") ---" + RESET);
            System.out.println("1. Ver Eventos Disponibles");
            System.out.println("2. Agregar Asiento al Carrito");
            System.out.println("3. Ver Mi Carrito");
            System.out.println("4. Checkout (Pagar Todo)");
            System.out.println("5. Ver Historial de Compras");
            System.out.println("6. Cerrar Sesión");
            System.out.print("Opción: ");
            String op = scanner.nextLine();

            switch (op) {
                case "1": listarEventos(); break;
                case "2": agregarAlCarritoGuiado(scanner, cliente); break;
                case "3": verCarrito(cliente); break;
                case "4": checkout(scanner, cliente); break;
                case "5": verHistorialCompras(scanner, cliente); break;
                case "6": return;
                default: System.out.println(RED + "Opción inválida." + RESET);
            }
        }
    }

    private void listarEventos() {
        List<Evento> eventos = eventoService.obtenerTodosLosEventos();
        System.out.println("\n--- LISTA DE EVENTOS ---");
        for (Evento e : eventos) {
            System.out.printf("ID: %d | %s | %s\n", e.getEventoId(), e.getNombre(), e.getEstado());
        }
    }

    private void agregarAlCarritoGuiado(Scanner scanner, Usuario cliente) {
        try {
            listarEventos();
            System.out.print("Ingrese ID del Evento: ");
            Long eventoId = Long.parseLong(scanner.nextLine());

            System.out.println("\n--- ZONAS ---");
            List<Zona> zonas = zonaService.obtenerZonasPorEvento(eventoId);
            for (Zona z : zonas) {
                System.out.printf("ID: %d | %s\n", z.getZonaId(), z.getNombre());
            }
            System.out.print("Ingrese ID de la Zona: ");
            Long zonaId = Long.parseLong(scanner.nextLine());

            System.out.println("\n--- ASIENTOS DISPONIBLES ---");
            List<Asiento> asientos = asientoService.obtenerAsientosPorZona(zonaId);
            
            Set<Long> asientosValidos = new HashSet<>();
            
            boolean hay = false;
            for (Asiento a : asientos) {
                if ("DISPONIBLE".equals(a.getEstado())) {
                    System.out.printf("%sID: %d | %s-%s | $%.2f%s\n", GREEN, a.getAsientoId(), a.getFila(), a.getNumeroAsiento(), a.getPrecio(), RESET);
                    hay = true;
                    asientosValidos.add(a.getAsientoId());
                }
            }
            if (!hay) {
                System.out.println(RED + "No hay asientos disponibles." + RESET);
                return;
            }

            System.out.print("Ingrese ID del Asiento: ");
            Long asientoId = Long.parseLong(scanner.nextLine());

            if (!asientosValidos.contains(asientoId)) {
                System.out.println(RED + "Error: El ID ingresado (" + asientoId + ") no corresponde a un asiento disponible en esta zona." + RESET);
                return;
            }

            if (transaccionService.bloquearAsientoYCrearCompra(cliente.getUsuarioId(), asientoId)) {
                System.out.println(GREEN + "¡Asiento agregado al carrito!" + RESET);
            } else {
                System.out.println(RED + "Error: Asiento no disponible." + RESET);
            }
        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void verCarrito(Usuario cliente) {
        try {
            List<Carrito> items = carritoService.obtenerCarritoPorUsuario(cliente.getUsuarioId());
            if (items.isEmpty()) {
                System.out.println(YELLOW + "Tu carrito está vacío." + RESET);
                return;
            }
            System.out.println("\n--- TU CARRITO ---");
            BigDecimal total = BigDecimal.ZERO;
            for (Carrito c : items) {
                String evento = c.getAsiento().getZona().getEvento().getNombre();
                System.out.printf("Evento: %s | Asiento: %s-%s | Precio: $%.2f\n", 
                    evento, c.getAsiento().getFila(), c.getAsiento().getNumeroAsiento(), c.getPrecio());
                total = total.add(c.getPrecio());
            }
            System.out.println(CYAN + "TOTAL A PAGAR: $" + total + RESET);
        } catch (Exception e) {
            System.out.println(RED + "Error al consultar carrito: " + e.getMessage() + RESET);
        }
    }

    private void checkout(Scanner scanner, Usuario cliente) {
        try {
            List<Carrito> items = carritoService.obtenerCarritoPorUsuario(cliente.getUsuarioId());
            if (items.isEmpty()) {
                System.out.println(YELLOW + "Nada que pagar." + RESET);
                return;
            }

            System.out.println("Procesando pago global del carrito...");
            if(transaccionService.finalizarCompraGlobal(cliente.getUsuarioId())) {
                System.out.println(GREEN + "¡COMPRA COMPLETADA EXITOSAMENTE! Revisa tu historial." + RESET);
            } else {
                System.out.println(RED + "Hubo un problema procesando el pago." + RESET);
            }

        } catch (Exception e) {
            System.out.println(RED + "Error: " + e.getMessage() + RESET);
        }
    }

    private void verHistorialCompras(Scanner scanner, Usuario cliente) {
        try {
            List<Compra> compras = compraService.listarComprasPorUsuario(cliente.getUsuarioId());

            if (compras.isEmpty()) {
                System.out.println(YELLOW + "No tienes compras registradas." + RESET);
                return;
            }

            System.out.println("\n--- TUS COMPRAS (HISTORIAL) ---");
            for (Compra c : compras) {
                System.out.printf("ID Compra: %d | Fecha: %s | Total: $%.2f\n", 
                    c.getCompraId(), 
                    c.getFechaCreacion().toLocalDate(), 
                    c.getMontoTotal());
            }

            System.out.print("\nIngrese ID de la Compra para ver entradas (o presione ENTER para volver): ");
            String input = scanner.nextLine();
            
            if (input.isEmpty()) return;

            try {
                Long compraId = Long.parseLong(input);
                // Validación para asegurar que la compra pertenece al usuario
                boolean esMia = compras.stream().anyMatch(c -> c.getCompraId().equals(compraId));
                
                if (esMia) {
                    verDetalleCompra(compraId);
                } else {
                    System.out.println(RED + "Compra no encontrada en tu historial." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "ID inválido." + RESET);
            }

        } catch (Exception e) {
            System.out.println(RED + "Error al obtener historial: " + e.getMessage() + RESET);
        }
    }

    private void verDetalleCompra(Long compraId) {
        try {
            List<Entrada> entradas = entradaService.listarEntradasPorCompra(compraId);
            
            if (entradas.isEmpty()) {
                System.out.println(YELLOW + "Esta compra no tiene entradas válidas." + RESET);
                return;
            }

            System.out.println(GREEN + "\n--- DETALLE DE ENTRADAS (Compra #" + compraId + ") ---" + RESET);
            for (Entrada e : entradas) {
                Asiento a = e.getAsiento();
                String nombreEvento = a.getZona().getEvento().getNombre();
                
                System.out.printf("Evento: %s | Zona: %s | Asiento: %s-%s | Precio: $%.2f\n",
                    nombreEvento, 
                    a.getZona().getNombre(), 
                    a.getFila(), 
                    a.getNumeroAsiento(), 
                    e.getPrecio());
            }
        } catch (Exception e) {
            System.out.println(RED + "Error al obtener detalles: " + e.getMessage() + RESET);
        }
    }
}