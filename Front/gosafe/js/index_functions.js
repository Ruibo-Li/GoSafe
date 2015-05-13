function myReport() {
    //hide the alert
    document.getElementById("submit_success").hidden = true;
    document.getElementById("invalid_date").hidden = true;
    document.getElementById('error').hidden = true;
    // Json object
    info.crime_time = document.getElementById("crime_time").value
    info.crime_date = document.getElementById("crime_date").value
    info.crime_type = document.getElementById("crime_type").value
    info.address = document.getElementById("crime_loc").value
    info.zipcode = document.getElementById("crime_zip").value
    if (info.crime_type == "select crime type") info.crime_type = ""
    // info.zipcode = document.getElementById("crime_zipcode").value


    document.getElementById("rpt_time").innerHTML = "Time: " + info.crime_time;
    document.getElementById("rpt_date").innerHTML = "Date: " + info.crime_date;
    document.getElementById("rpt_type").innerHTML = "Crime Type: " + info.crime_type;
    document.getElementById("rpt_loc").innerHTML = "Location: " + info.address;
    document.getElementById("rpt_zip").innerHTML = "Zipcode: " + info.zipcode;

}

function mySearch(){
    var zipcode = document.getElementById("searchInput").value;
    var urlstring = "map.html?zipcode=" + zipcode;
    window.location = urlstring;
}
