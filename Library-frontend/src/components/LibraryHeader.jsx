import { Link } from "react-router"

function LibraryHeader() {
    const libraryName = "Library Management System"

    return (
        <div>
            <header>

                <h1>{libraryName}</h1>
                <Link to="/books" style={{padding : "10px"}}>BOOKS</Link>
                <Link to="/office" style={{padding : "10px"}}>OFFICE</Link>
                <Link to="/appointment" style={{padding : "10px"}}>APPOINTMENT</Link>
                <Link to="/doctor" style={{padding : "10px"}}>DOCTOR</Link>
                <Link to="/officeList" style={{padding : "10px"}}>OFFICELIST</Link>
                <Link to="/" style={{padding : "10px"}}>LOGOUT</Link>
                
                <p>This is Library system that will help you</p>
            </header>
        </div>
    )
}
export default LibraryHeader