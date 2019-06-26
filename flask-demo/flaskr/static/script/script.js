function readURL(input) {
  document.getElementById("preview").style.display = "block";
  document.getElementById("official").style.display = "none";
  
  if (input.files && input.files[0]) {
     var reader = new FileReader();
     
     reader.onload = function(e) {
        document.getElementById("preview").src = e.target.result;
        document.getElementById("result").innerHTML = "";
     }
     
     reader.readAsDataURL(input.files[0]);
  }
}