import os
import test
from flask import Flask, flash, request, redirect, url_for, render_template
from werkzeug.utils import secure_filename
from flask import send_from_directory
import time

app = Flask(__name__)

# SET HOST AND PORT
HOST="127.0.0.1"
PORT="5000"

# SET THE FOLDER TO UPLOAD FILE
UPLOAD_FOLDER = os.path.join('static', 'uploads')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

# LIST OF ALLOWED FILE EXTENSION
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])

# CHECK IF FILE EXTENTION IS ALLOWED OR NOT
def allowed_file(filename):
   return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# UPLOAD PROCESS
@app.route('/', methods = ['GET', 'POST'])
def upload_file():
   if request.method == 'POST':
      file = request.files['file']
      lang = request.form.get('lang')
      if file and allowed_file(file.filename):

         # SSH PASSWORD
         password = 'Cv6!Z=ZG?5)4QN}R'
         filename = secure_filename(file.filename)

         # SAVE FILE TO SERVER
         os.popen("rm -r ./static/uploads/*")
         file.save(os.path.join(UPLOAD_FOLDER, filename))
         f = os.popen("sshpass -p '" + password + "' scp -P 25 " + os.path.join(app.config['UPLOAD_FOLDER'], filename) + ' root@45.32.39.232:~/Document_Reader/Upload')
         time.sleep(3)

         # GET THE RESULT BY API
         result = test.detect("./Upload/" + filename + ":" + lang)

         # SHOW THE RESULT
         full_filename = os.path.join('/' + app.config['UPLOAD_FOLDER'], filename)
         return render_template("home.html", text_result = result, image = full_filename)

   return render_template('home.html')

if __name__ == "__main__":
   app.run(host=HOST, port=PORT, debug=True)