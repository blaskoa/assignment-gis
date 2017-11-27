using System.Collections.Generic;
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
      public IEnumerable<string> Get()
      {
         return _repository.GetOffice();
      }

      // GET: api/Offices/5
      [HttpGet]
      public IEnumerable<string> Get(GeoPoint point)
      {
         return _repository.GetOfficesFiltered(point.Latitude, point.Longitude);
      }

      // POST: api/Offices
      [HttpPost]
      public IEnumerable<string> Post([FromBody]GeoPoint point)
      {
         return _repository.GetOfficesFiltered(point.Latitude, point.Longitude);
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
