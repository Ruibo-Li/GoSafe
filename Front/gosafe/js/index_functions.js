function myReport() {
    //hide the alert
    document.getElementById("submit_success").hidden = true;
    document.getElementById("invalid_date").hidden = true;
    // Json object
    info.crime_time = document.getElementById("crime_time").value
    info.crime_date = document.getElementById("crime_date").value
    info.type = document.getElementById("crime_type").value
    info.address = document.getElementById("crime_loc").value
    info.zipcode = document.getElementById("crime_zip").value
    if (info.type == "select crime type") info.type = ""
    // info.zipcode = document.getElementById("crime_zipcode").value


    document.getElementById("rpt_time").innerHTML = "Time: " + info.crime_time;
    document.getElementById("rpt_date").innerHTML = "Date: " + info.crime_date;
    document.getElementById("rpt_type").innerHTML = "Crime Type: " + info.type;
    document.getElementById("rpt_loc").innerHTML = "Location: " + info.address;
    document.getElementById("rpt_zip").innerHTML = "Zipcode: " + info.zipcode;

}

function checkDate(){
    var today = new Date();
    var crime_date_dt = new Date(info.crime_date);
    if (crime_date_dt > today){
        return 0;
    }
    else
        return 1;
};

function plot() {
    var request = new XMLHttpRequest();
    info.description = document.getElementById("crime_descp").value
    console.log(info)
    request.onload = reqListener;
    request.open("POST", "ReportCrime", true);
    request.send(JSON.stringify(info));
};

function reqListener() {
    var data = JSON.parse(this.responseText);
    console.log(data);
};