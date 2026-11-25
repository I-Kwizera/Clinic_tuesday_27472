import React from 'react'
import LibraryButton from './LibraryButton';
import axios from 'axios';
import { useState } from 'react';
import OfficeList from './OfficeList';
import LibraryHeader from './LibraryHeader';

function Office() {

    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");
    const [deleteNumber, setDeleteNumber] = useState("");

    async function saveOffice(){
        const response = await axios.post("http://localhost:8083/api/office/save",
        {
            name: officeName,
            officeNumber: parseInt(officeNumber),
        }
    )
    if(response.status == 201){
        alert("Office saved successfully")
    }else
        {
        alert("Saving Office failed")
        }
    }

    async function deleteOffice(){
        const response = await axios.delete("http://localhost:8083/api/office/delete/" + deleteNumber)
        alert(response.data)
    }

    return (
    <div>
        <LibraryHeader />
        <h1>SAVE OFFICE</h1>
        <label>Office Name</label><input type='text' onChange={e=>setOfficeName(e.target.value)} /> <br/>
        <label>Office Number</label><input type='text' onChange={e=>setOfficeNumber(e.target.value)} /> <br/>
        <LibraryButton buttonLabel="SAVE OFFICE" onClickButton={saveOffice}/>
        
        <h2>DELETE OFFICE</h2>
        <input type='text' onChange={e=>setDeleteNumber(e.target.value)}/>
        <LibraryButton buttonLabel="DELETE" onClickButton={deleteOffice}/>
    </div>
    )
}

export default Office
