import React from 'react'
import axios from 'axios';
import { useState, useEffect } from 'react';
import Office from './Office';
import LibraryHeader from './LibraryHeader';

function OfficeList() {

    const [offices, setOffices] = useState([]);
    const [searchText, setSearchText] = useState("");

    async function loadOffices(){
        const response = await axios.get("http://localhost:8083/api/office/all")
        setOffices(response.data)
    }

    useEffect(() => {
        loadOffices()
    }, [])

    async function deleteOffice(officeNumber){
        const response = await axios.delete("http://localhost:8083/api/office/delete/" + officeNumber)
        alert(response.data)
        loadOffices()
    }

    const filteredOffices = offices.filter(office =>
        office.name.toLowerCase().includes(searchText.toLowerCase()) ||
        String(office.officeNumber).includes(searchText)
    )

    return (
    <div>
        <LibraryHeader />
        <h1>ALL OFFICES</h1>

        <input type='text' placeholder='Search by office name or number'
            onChange={e=>setSearchText(e.target.value)} />

        <table border="1">
            <thead>
                <tr>
                    <th>Office Name</th>
                    <th>Office Number</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                {filteredOffices.map(office => (
                    <tr key={office.id}>
                        <td>{office.name}</td>
                        <td>{office.officeNumber}</td>
                        <td>
                            <button onClick={() => deleteOffice(office.officeNumber)}>Delete</button>
                            <button>Update</button>
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    </div>
    )
}

export default OfficeList