# API REST para el Módulo de Ventas usando Flask, SQLAlchemy y PyMySQL
# Este archivo contiene la configuración de la BD (más robusta) y todos los endpoints CRUD.

from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import date
import json

# --- Configuración de la Aplicación Flask ---
app = Flask(__name__)

# --- Configuración de la Conexión a MySQL con SQLAlchemy ---
# ¡IMPORTANTE! Se ha establecido la contraseña como vacía (solo 'root:@')
# Asegúrate de que tu BD se llame 'modulo_ventas_db'
# Esta configuración es común para instalaciones por defecto de XAMPP/WAMP
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:@localhost/modulo_ventas_db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

db = SQLAlchemy(app)

# --- Modelo de Base de Datos (Clase Venta) ---
class Venta(db.Model):
    """Define la tabla 'ventas' en la base de datos."""
    __tablename__ = 'ventas'
    id = db.Column(db.Integer, primary_key=True)
    nombre_cliente = db.Column(db.String(100), nullable=False)
    total_venta = db.Column(db.Float, nullable=False)
    fecha_venta = db.Column(db.Date, nullable=False)
    estado = db.Column(db.String(50), nullable=False)

    def to_dict(self):
        """Convierte el objeto Venta en un diccionario serializable para JSON."""
        return {
            "id": self.id,
            "clienteNombre": self.nombre_cliente,
            "totalVenta": float(self.total_venta),
            # Convierte el objeto date a string ISO 8601
            "fechaVenta": self.fecha_venta.isoformat() if isinstance(self.fecha_venta, date) else str(self.fecha_venta),
            "estado": self.estado
        }

# --- Rutas / Endpoints de la API REST ---

# 1. POST (Crear) y GET (Listar todos)
@app.route('/api/v1/ventas', methods=['POST', 'GET'])
def ventas():
    # --- GET: Obtener todas las Ventas (READ ALL) ---
    if request.method == 'GET':
        try:
            # Consulta todas las ventas usando el modelo SQLAlchemy
            ventas_db = db.session.execute(db.select(Venta)).scalars().all()
            # Convierte la lista de objetos Venta a una lista de diccionarios JSON
            ventas_list = [venta.to_dict() for venta in ventas_db]
            return jsonify(ventas_list), 200

        except Exception as e:
            print(f"Error al listar ventas: {e}")
            return jsonify({"error": "Error interno del servidor al listar ventas"}), 500

    # --- POST: Crear Nueva Venta (CREATE) ---
    elif request.method == 'POST':
        try:
            data = request.get_json()
            
            # Validación de campos
            if not all(k in data for k in ('clienteNombre', 'totalVenta', 'fechaVenta', 'estado')):
                 return jsonify({"error": "Faltan campos obligatorios"}), 400

            # --- VERIFICACIÓN DE FORMATO DE FECHA/DATOS ---
            try:
                # Intenta convertir la fecha. Si falla, genera una excepción de valor.
                fecha_obj = date.fromisoformat(data['fechaVenta'])
            except ValueError:
                return jsonify({"error": "Formato de fecha de venta inválido. Use YYYY-MM-DD."}), 400
            
            try:
                # Intenta convertir el total a float
                total_float = float(data['totalVenta'])
            except ValueError:
                return jsonify({"error": "El totalVenta debe ser un número válido."}), 400


            # Crear una nueva instancia del modelo Venta
            nueva_venta = Venta(
                nombre_cliente=data['clienteNombre'],
                total_venta=total_float,
                # Asegura que la fecha se guarde como objeto Date
                fecha_venta=fecha_obj,
                estado=data['estado']
            )

            # Agregar y guardar en la base de datos
            db.session.add(nueva_venta)
            db.session.commit()
            
            # Retorna el objeto recién creado
            return jsonify(nueva_venta.to_dict()), 201

        except Exception as e:
            # Captura cualquier otro error de base de datos o lógica que no sea de formato de datos
            print(f"Error al crear venta (General DB/Lógica): {e}")
            db.session.rollback() # Si hay un error, revierte la transacción
            return jsonify({"error": "Error interno del servidor al crear venta (Revise la consola del servidor para detalles)"}), 500

# 2. GET (Leer por ID), PUT (Actualizar), DELETE (Eliminar)
@app.route('/api/v1/ventas/<int:id>', methods=['GET', 'PUT', 'DELETE'])
def venta_by_id(id):
    
    # Busca la venta por ID
    venta = db.session.execute(db.select(Venta).filter_by(id=id)).scalar_one_or_none()

    if not venta:
        return jsonify({"error": f"Venta con ID {id} no encontrada"}), 404
    
    # --- GET: Obtener Venta por ID (READ ONE) ---
    if request.method == 'GET':
        return jsonify(venta.to_dict()), 200

    # --- PUT: Actualizar Venta (UPDATE) ---
    elif request.method == 'PUT':
        try:
            data = request.get_json()
            
            # Actualiza los campos del objeto Venta
            venta.nombre_cliente = data.get('clienteNombre', venta.nombre_cliente)
            venta.total_venta = data.get('totalVenta', venta.total_venta)
            # Actualiza la fecha solo si se proporciona
            if 'fechaVenta' in data:
                 # Añadir manejo de error para el PUT también
                try:
                    venta.fecha_venta = date.fromisoformat(data['fechaVenta'])
                except ValueError:
                    return jsonify({"error": "Formato de fecha de venta inválido. Use YYYY-MM-DD."}), 400

            venta.estado = data.get('estado', venta.estado)

            db.session.commit()

            return jsonify(venta.to_dict()), 200

        except Exception as e:
            print(f"Error al actualizar venta: {e}")
            db.session.rollback()
            return jsonify({"error": "Error interno del servidor al actualizar venta"}), 500

    # --- DELETE: Eliminar Venta (DELETE) ---
    elif request.method == 'DELETE':
        try:
            db.session.delete(venta)
            db.session.commit()
            return '', 204 # 204 No Content para eliminación exitosa

        except Exception as e:
            print(f"Error al eliminar venta: {e}")
            db.session.rollback()
            return jsonify({"error": "Error interno del servidor al eliminar venta"}), 500

# --- Inicio del Servidor ---
if __name__ == '__main__':
    # Crea las tablas en la base de datos si no existen (usa el modelo Venta)
    # Esto es crucial para que la aplicación funcione por primera vez
    with app.app_context():
        db.create_all() 
    
    # Ejecuta el servidor en modo debug para desarrollo
    app.run(debug=True, port=5000)