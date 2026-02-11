package model;

public class Producto {
    private String codigo;
    private String nombre;
    private String descripcion;
    private int stock;
    private double precio;
    private int stockMinimo;
    private String pasillo;
    private String estante;
    private String posicion;

    public Producto(String codigo, String nombre, String descripcion, int stock, double precio, int stockMinimo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio = precio;
        this.stockMinimo = stockMinimo;
        this.pasillo = "";
        this.estante = "";
        this.posicion = "";
    }

    public Producto(String codigo, String nombre, String descripcion, int stock, double precio, int stockMinimo, String pasillo, String estante, String posicion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio = precio;
        this.stockMinimo = stockMinimo;
        this.pasillo = pasillo != null ? pasillo : "";
        this.estante = estante != null ? estante : "";
        this.posicion = posicion != null ? posicion : "";
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getPasillo() { return pasillo; }
    public void setPasillo(String pasillo) { this.pasillo = pasillo != null ? pasillo : ""; }

    public String getEstante() { return estante; }
    public void setEstante(String estante) { this.estante = estante != null ? estante : ""; }

    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion != null ? posicion : ""; }

    public String getUbicacionCompleta() {
        if (pasillo.isEmpty() && estante.isEmpty() && posicion.isEmpty()) {
            return "Sin ubicación";
        }
        String p = pasillo.isEmpty() ? "?" : pasillo;
        String e = estante.isEmpty() ? "?" : estante;
        String pos = posicion.isEmpty() ? "?" : posicion;
        return p + "-" + e + "-" + pos;
    }

    public boolean tieneUbicacion() {
        return !pasillo.isEmpty() || !estante.isEmpty() || !posicion.isEmpty();
    }

    @Override
    public String toString() {
        String ubicacion = getUbicacionCompleta();
        return nombre + " (" + codigo + ") - " + ubicacion;
    }
}
