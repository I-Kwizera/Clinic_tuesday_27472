import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import LibraryHeader from './components/LibraryHeader'
import LibraryBooks from './components/LibraryBooks'
import LibraryFooter from './components/LibraryFooter'
import LibraryButton from './components/LibraryButton'
import LibraryLogin from './components/LibraryLogin'
import './App.css'
import Office from './components/Office'
import OfficeList from './components/OfficeList'
import { Route, Routes } from 'react-router'
import Doctor from './components/Doctor'
import Appointments from './components/Appointments'




function App() {
  return (
    
    <div>

  <Routes>
    <Route path="/" element={<LibraryLogin />}/>
    <Route path="/office" element={<Office />}/>
    <Route path="/books" element={<LibraryBooks />}/>
    <Route path="/officeList" element={<OfficeList/>}/>
    <Route path="/appointment" element={<Appointments />}/>
    <Route path="/doctor" element={<Doctor/>}/>
    <Route path="*" element={<LibraryLogin />}/>

  </Routes>
      </div>
  )

}
export default App 
