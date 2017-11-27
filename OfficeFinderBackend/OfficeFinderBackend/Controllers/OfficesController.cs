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
      public IEnumerable<Office> Get()
      {
         return _repository.GetOffice();
      }

      // POST: api/Offices
      [HttpPost]
      public IEnumerable<Office> Post([FromBody]GeoPoint point)
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
