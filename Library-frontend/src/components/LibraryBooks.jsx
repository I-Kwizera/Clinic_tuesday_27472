import React from 'react'
import LibraryCard from './LibraryCard'
import LibraryHeader from './LibraryHeader'
function LibraryBooks() {

const books = [
  {
    code : "BIO101",
    name  : "Biology",
    price : 1500
  },
{
  code : "CHEM101",
  name  : "Chemistry",
  price : 500
},
{
  code : "MATH101",
  name  : "Mathematics",
  price : 1000
},
{
  code : "COMP101",
  name  : "Software Engineering",
  price : 2000
},
]

  return (
    <div>
      <LibraryHeader />
      <ul>
        
        {
          books.map(book => <LibraryCard bookInfo={book}/>)
        }

      </ul>
      <div>
        The total price for {books.length} books is : {books.reduce((acc, book) => acc + book.price, 0)}
      </div>
    </div>
  )
}

export default LibraryBooks
