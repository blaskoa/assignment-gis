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
      private readonly OfficeRepository _repository;

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
   }
}
