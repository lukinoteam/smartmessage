function preview(){
    file = document.getElementById('inputFile');

    if (file.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            document.getElementById("preview").src = e.target.result;
        };
        reader.readAsDataURL(file.files[0]);
    }
}