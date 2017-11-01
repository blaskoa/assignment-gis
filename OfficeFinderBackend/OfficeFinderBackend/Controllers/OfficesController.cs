using System.Collections.Generic;
using Microsoft.AspNetCore.Mvc;

namespace OfficeFinderBackend.Controllers
{
    [Produces("application/json")]
    [Route("api/Offices")]
    public class OfficesController : Controller
    {
        // GET: api/Offices
        [HttpGet]
        public IEnumerable<string> Get()
        {
            return new[] { "value1", "value2" };
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
