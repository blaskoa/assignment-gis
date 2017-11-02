using System.Collections.Generic;
using GeoJSON.Net.Geometry;
using Microsoft.AspNetCore.Mvc;
using OfficeFinderBackend.Models;
using OfficeFinderBackend.Repositories;

namespace OfficeFinderBackend.Controllers
{
   [Produces("application/json")]
   [Route("api/Offices")]
   public class OfficesController : Controller
   {
      private OfficeRepository _repository;

      public OfficesController()
      {
         _repository = new OfficeRepository();
      }

      // GET: api/Offices
      [HttpGet]
      public Office Get()
      {
         return _repository.GetOffice();
      }

      // GET: api/Offices/5
      [HttpGet("{id}", Name = "Get")]
      public string Get(int id)
      {
         return "value";
      }

      // POST: api/Offices
      [HttpPost]
      public void Post([FromBody]string value)
      {
      }

      // PUT: api/Offices/5
      [HttpPut("{id}")]
      public void Put(int id, [FromBody]string value)
      {
      }

      // DELETE: api/ApiWithActions/5
      [HttpDelete("{id}")]
      public void Delete(int id)
      {
      }
   }
}
