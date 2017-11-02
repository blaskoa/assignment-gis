using System.Collections.Generic;
using GeoJSON.Net.Geometry;

namespace OfficeFinderBackend.Models
{
   public class Office
   {
      public List<Polygon> Polygons { get; set; }
   }
}