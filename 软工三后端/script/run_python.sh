cd /nioCoin/backend/product/FlaskNLPServer || exit 1
conda activate Py36 || exit 1
nohup FLASK_APP=flask-nlp-server.application FLASK_ENV=production flask run >py_log 2>&1 &
