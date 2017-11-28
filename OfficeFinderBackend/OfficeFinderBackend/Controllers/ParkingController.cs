using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;
using OfficeFinderBackend.Models;
using OfficeFinderBackend.Repositories;

namespace OfficeFinderBackend.Controllers
{
   [Produces("application/json")]
   [Route("api/Parking")]
   public class ParkingController : Controller
   {
      private readonly ParkingRepository _repository;

      public ParkingController()
      {
         _repository = new ParkingRepository();
      }

      [HttpPost]
      public IEnumerable<Parking> Post([FromBody]ParkingRequest request)
      {
         return _repository.GetParking(request);
      }
   }
}