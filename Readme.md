Proyecto de Servicios Web - Módulo de Ventas (SENA - GA7-220501096-AA5-EV03)

Este repositorio contiene la implementación de una API RESTful para el módulo de ventas, desarrollada con el framework Flask en Python, utilizando Flask-SQLAlchemy para la persistencia de datos en MySQL. También incluye una interfaz gráfica básica en HTML/JavaScript para demostrar el consumo de la API.

Tecnologías Utilizadas

Backend: Python 3.x, Flask, Flask-SQLAlchemy, PyMySQL.

Base de Datos: MySQL.

Frontend: HTML, JavaScript (Fetch API), Tailwind CSS.

Estructura del Proyecto

app.py: Contiene toda la lógica del backend (modelo de datos, configuración de la base de datos y endpoints CRUD).

index.html: Interfaz gráfica simple para el consumo de la API (Listar, Crear, Eliminar).

API_DOCUMENTACION.md: Documentación detallada de cada endpoint de la API.

requirements.txt: Lista de dependencias de Python.

Configuración y Ejecución del Backend

1. Requisitos Previos

Asegúrate de tener instalado:

Python 3.x

MySQL Server (o XAMPP/WAMP/MAMP)

Un gestor de paquetes de Python como pip.

2. Base de Datos

Crea una base de datos en MySQL (ej: usando phpMyAdmin o la consola) llamada: modulo_ventas_db.

Ajusta las credenciales en el archivo app.py si tu usuario root tiene contraseña:

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:TU_CONTRASEÑA_AQUÍ@localhost/modulo_ventas_db'


3. Instalación de Dependencias

Ejecuta el siguiente comando para instalar las librerías necesarias:

pip install Flask Flask-SQLAlchemy PyMySQL marshmallow


4. Ejecución del Servidor Flask

Ejecuta el archivo app.py. Esto iniciará el servidor en http://127.0.0.1:5000 y creará la tabla ventas si no existe.

python app.py


Consumo de la API

Una vez que el servidor esté corriendo, puedes:

Prueba Manual: Utilizar herramientas como Thunder Client o Postman para probar los endpoints documentados en API_DOCUMENTACION.md.

Prueba Gráfica: Abrir el archivo index.html en tu navegador para interactuar con la API visualmente.