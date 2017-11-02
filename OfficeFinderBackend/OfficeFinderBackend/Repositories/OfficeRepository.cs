using System.Collections.Generic;
using System.Linq;
using Dapper;
using GeoJSON.Net.Geometry;
using Newtonsoft.Json;
using OfficeFinderBackend.Models;

namespace OfficeFinderBackend.Repositories
{
   public class OfficeRepository : BaseRepository
   {
      private const string testQuery = 
         @"SELECT ST_AsGeoJSON(way)::json
	         FROM public.planet_osm_polygon
            where amenity= 'parking'
            and st_dwithin(way, st_setsrid(st_makepoint(16, 49), 4326), 5)
            limit 100";

      public Office GetOffice()
      {
         var result = new Office();
         IEnumerable<string> serializedResult = Connection.Query<string>(testQuery);
         result.Polygons = serializedResult.Select(JsonConvert.DeserializeObject<Polygon>).ToList();
         return result;
      }
   }
}