from flask import Flask, jsonify
import requests
import pymysql

# Configuración inicial de la aplicación y base de datos
# Inicializamos la aplicación Flask
app = Flask(__name__)

# Credenciales de la base de datos apuntando al puerto mapeado en Docker (3506)
DB_HOST = "localhost"
DB_PORT = 3506
DB_USER = "root"
DB_PASSWORD = "eneas2805" 
DB_NAME = "basicosd"

# Definición del endpoint principal. Se invocará desde el backend en Java mediante una petición GET
@app.route('/pokemon/<nombre>')
def get_pokemon(nombre):
    
    # Simuladores de errores para validar la gestión de excepciones en el TFG
    # Condición para forzar un error de lectura de archivos inexistentes
    if nombre == "error_archivo":
        try:
            with open("archivo_inventado.txt", "r") as f:
                data = f.read()
        except FileNotFoundError as e:
            return jsonify({"error": f"Excepción de Archivos: No se pudo leer el archivo local. Detalle nativo: {str(e)}"}), 500
            
    # Condición para forzar un error de conexión a la base de datos apuntando a un puerto incorrecto
    if nombre == "error_db":
        try:
            conexion_falsa = pymysql.connect(host=DB_HOST, port=9999, user=DB_USER, password=DB_PASSWORD, database=DB_NAME)
        except pymysql.err.OperationalError as e:
            return jsonify({"error": f"Excepción de Datos: Fallo al conectar con MySQL. Detalle nativo: {str(e)}"}), 500
            
    # Bloque de persistencia: Guardado del historial de búsquedas
    try:
        # Establecemos la conexión con MySQL para registrar el Pokémon consultado
        conexion = pymysql.connect(host=DB_HOST, port=DB_PORT, user=DB_USER, password=DB_PASSWORD, database=DB_NAME)
        cursor = conexion.cursor()
        # Se crea la tabla automáticamente si es la primera ejecución del sistema
        cursor.execute("CREATE TABLE IF NOT EXISTS historial_busquedas (id INT AUTO_INCREMENT PRIMARY KEY, pokemon VARCHAR(50))")
        cursor.execute("INSERT INTO historial_busquedas (pokemon) VALUES (%s)", (nombre,))
        conexion.commit()
        conexion.close()
    except Exception as e:
        # Tolerancia a fallos: Si la conexión falla, se captura la excepción para no interrumpir el flujo principal de la aplicación
        print(f"Aviso: No se pudo registrar el historial. Detalle: {e}")
        
    # Consulta principal a la PokeAPI externa
    url = f"https://pokeapi.co/api/v2/pokemon/{nombre.lower()}"
    try:
        response = requests.get(url)
        # Manejo del caso en el que el Pokémon no se encuentre en la API (Error HTTP 404)
        if response.status_code == 404:
            return jsonify({"error": f"Excepción de API (404): La PokeAPI indica que '{nombre}' no existe."}), 404
        
        # Si la petición es exitosa, extraemos los datos en formato JSON
        data = response.json()
        
        # Traducción al español y formateo de la respuesta
        # Establecemos una descripción por defecto en caso de que la API no disponga de ella
        descripcion = "Descripción no disponible."
        try:
            # Realizamos una petición adicional para obtener los textos descriptivos de la especie
            species_url = data['species']['url']
            species_response = requests.get(species_url)
            if species_response.status_code == 200:
                species_data = species_response.json()
                # Iteramos sobre las descripciones hasta localizar la versión en español
                for entry in species_data['flavor_text_entries']:
                    if entry['language']['name'] == 'es': 
                        # Eliminamos los saltos de línea y caracteres de control del texto original
                        descripcion = entry['flavor_text'].replace('\n', ' ').replace('\f', ' ')
                        break
        except Exception:
            pass # Si esta petición secundaria falla, se mantiene la descripción por defecto y la ejecución continúa

        # Construimos y retornamos el objeto JSON final únicamente con los campos requeridos por el backend
        return jsonify({
            "name": data["name"],
            "height": data["height"],
            "weight": data["weight"],
            "type": data["types"][0]["type"]["name"],
            "image": data["sprites"]["front_default"],
            "description": descripcion
        })
        
    # Manejo de excepciones de red en caso de que la PokeAPI no esté disponible
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Excepción de Red: Fallo al conectar con PokeAPI. Detalle: {str(e)}"}), 503

# Ejecución del microservicio en el puerto 5000, accesible desde cualquier interfaz de red local
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)