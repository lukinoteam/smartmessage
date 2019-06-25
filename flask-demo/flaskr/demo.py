import os
import test
from flask import Flask, flash, request, redirect, url_for, render_template
from werkzeug.utils import secure_filename
from flask import send_from_directory

app = Flask(__name__)

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
      if file and allowed_file(file.filename):
         filename = secure_filename(file.filename)
         file.save(os.path.join(UPLOAD_FOLDER, filename))
         password = 'Cv6!Z=ZG?5)4QN}R'
         f = os.popen("sshpass -p '" + password + "' scp -P 25 " + os.path.join(app.config['UPLOAD_FOLDER'], filename) + ' root@45.32.39.232:~/Document_Reader/Upload')
      
         return redirect(url_for('show_image', filename=filename))

   return render_template('upload.html')

# SHOW THE RESULT
@app.route('/uploads/<filename>', methods = ['GET', 'POST'])
def show_image(filename):
   full_filename = os.path.join('/' + app.config['UPLOAD_FOLDER'], filename)
   if request.method == 'POST':
      result = test.detect("./Upload/" + filename)
      return render_template("result.html", text_result = result, image = full_filename)

   return render_template("result.html", image = full_filename)

   