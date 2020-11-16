/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function cambio(){
    var selected = PF('listRec').value;
    //alert(selected);
    
    if(selected.toString() === "Por Mes"){
         
    }else if(selected.toString() === "Veces por Mes"){
       $('veces').attr('disabled',false);
       
    }else if(selected.toString() === "Veces por Mes con frecuencia"){
       
    }

    
 }
   