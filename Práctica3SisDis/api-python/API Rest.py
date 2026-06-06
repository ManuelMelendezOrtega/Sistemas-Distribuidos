from flask import Flask, jsonify
import requests
import pymysql

app = Flask(__name__)

DB_HOST = "localhost"
DB_PORT = 3506
DB_USER = "root"
DB_PASSWORD = "eneas2805" 
DB_NAME = "basicosd"

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
        
        # Buscar la descripción en la API de especies
        descripcion = "Descripción no disponible."
        try:
            species_url = data['species']['url']
            species_response = requests.get(species_url)
            if species_response.status_code == 200:
                species_data = species_response.json()
                for entry in species_data['flavor_text_entries']:
                    if entry['language']['name'] == 'es': # Buscamos el texto en español
                        descripcion = entry['flavor_text'].replace('\n', ' ').replace('\f', ' ')
                        break
        except Exception:
            pass

        return jsonify({
            "name": data["name"],
            "height": data["height"],
            "weight": data["weight"],
            "type": data["types"][0]["type"]["name"],
            "image": data["sprites"]["front_default"],
            "description": descripcion
        })
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Excepción de Red: Fallo al conectar con PokeAPI. Detalle: {str(e)}"}), 503

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)