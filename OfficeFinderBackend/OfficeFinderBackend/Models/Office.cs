using System.Collections.Generic;

namespace OfficeFinderBackend.Models
{
   public class Office
   {
      public long Id { get; set; }
      public string Name { get; set; }
      public string GeoJson { get; set; }
      public IEnumerable<GeoPointString> GeoPointStrings { get; set; }
   }
}