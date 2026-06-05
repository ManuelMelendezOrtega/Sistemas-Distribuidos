from flask import Flask, jsonify
import requests
import pymysql

# Archivo principal de la API en Python. 
# Se encarga de recibir peticiones, guardar el historial en la base de datos y consultar la PokeAPI.

app = Flask(__name__)

DB_HOST = "localhost"
DB_PORT = 3506
DB_USER = "root"
DB_PASSWORD = "eneas2805" 
DB_NAME = "basicosd"

# Método principal que recibe el nombre de un Pokémon desde el frontend en Java.
# Simula errores si recibe palabras clave, guarda el historial en la BD y devuelve los datos del Pokémon.
@app.route('/pokemon/<nombre>')
def get_pokemon(nombre):
    
    if nombre == "error_archivo":
        try:
            with open("archivo_inventado.txt", "r") as f:
                data = f.read()
        except FileNotFoundError as e:
            return jsonify({"error": f"Excepción de Archivos: No se pudo leer el archivo local. Detalle nativo: {str(e)}"}), 500
            
    if nombre == "error_db":
        try:
            conexion_falsa = pymysql.connect(host=DB_HOST, port=9999, user=DB_USER, password=DB_PASSWORD, database=DB_NAME)
        except pymysql.err.OperationalError as e:
            return jsonify({"error": f"Excepción de Datos: Fallo al conectar con MySQL. Detalle nativo: {str(e)}"}), 500
            
    try:
        conexion = pymysql.connect(host=DB_HOST, port=DB_PORT, user=DB_USER, password=DB_PASSWORD, database=DB_NAME)
        cursor = conexion.cursor()
        cursor.execute("CREATE TABLE IF NOT EXISTS historial_busquedas (id INT AUTO_INCREMENT PRIMARY KEY, pokemon VARCHAR(50))")
        cursor.execute("INSERT INTO historial_busquedas (pokemon) VALUES (%s)", (nombre,))
        conexion.commit()
        conexion.close()
    except Exception as e:
        print(f"Aviso: No se pudo guardar el historial. Detalle: {e}")
        
    url = f"https://pokeapi.co/api/v2/pokemon/{nombre.lower()}"
    try:
        response = requests.get(url)
        if response.status_code == 404:
            return jsonify({"error": f"Excepción de API (404): La PokeAPI dice que '{nombre}' no existe."}), 404
        
        data = response.json()
        return jsonify({
            "name": data["name"],
            "height": data["height"],
            "weight": data["weight"],
            "type": data["types"][0]["type"]["name"]
        })
    except Exception as e:
        return jsonify({"error": f"Error crítico al conectar con PokeAPI: {str(e)}"}), 503

if __name__ == '__main__':
    # Arrancamos Flask en el puerto 5000
    app.run(port=5000)